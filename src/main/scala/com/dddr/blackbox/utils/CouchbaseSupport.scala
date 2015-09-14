package com.dddr.blackbox.utils

import java.util.NoSuchElementException

import akka.http.scaladsl.marshalling.{Marshal, Marshaller}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import com.couchbase.client.java.CouchbaseCluster
import com.couchbase.client.java.document.RawJsonDocument
import com.couchbase.client.java.document.json.JsonArray
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment
import com.couchbase.client.java.view.{AsyncViewRow, Stale, ViewQuery}
import com.dddr.blackbox.http.Protocol
import com.dddr.blackbox.http.routes.BaseServiceRoute
import rx.RxReactiveStreams
import rx.lang.scala.JavaConversions.{toJavaObservable, toScalaObservable}
import rx.lang.scala.Observable

import scala.collection.JavaConversions._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
 * Created by Jason Martens on 9/8/15.
 */
// Keep the env in an object so it is only created once
object CouchbaseSupport {
  val env = DefaultCouchbaseEnvironment.builder().build()
}

trait CouchbaseSupport extends Config with Protocol with BaseServiceRoute {

  // Reuse the env here
  val cluster = CouchbaseCluster.create(CouchbaseSupport.env, couchbaseConfig.getString("hostname"))

  val bucket = cluster.openBucket(
    couchbaseConfig.getString("bucket"),
    couchbaseConfig.getString("password"))

  /**
   * Convert an Observable with a single element into a Future
   * @param observable The source to convert
   * @tparam T The type of the resulting Future
   * @return The future
   */
  def observableToFuture[T](observable: Observable[T]): Future[T] = {
    Source(RxReactiveStreams.toPublisher(observable)).runWith(Sink.head)
  }

  def observableToSource[T](observable: rx.lang.scala.Observable[T]): Source[T, Any] = {
    Source(RxReactiveStreams.toPublisher(observable))
  }


  /**
   * Use the akka-http marshalling system to marshal a case class into json
   *
   * @param entity The case class object to marshal
   * @param m An implicit marshaller to do the conversion
   * @tparam T The case class type
   * @return The json representation of the entity
   */
  def marshalEntity[T](entity: T)(implicit m: Marshaller[T, MessageEntity]): Future[String] = {
    // MessageEntity is used as a placeholder to get json as the marshalled value
    Marshal(entity).to[MessageEntity].flatMap { entity =>
      // Convert the MessageEntity to a string after marshalling
      entity.dataBytes.map(byteString => byteString.map(byte => byte.toChar))
        .map(charSeq => charSeq.mkString).runWith(Sink.head)
    }
  }

  def unmarshalEntity[T](data: MessageEntity)(implicit m: Unmarshaller[MessageEntity, T]): Future[T] = {
    Unmarshal(data).to[T]
  }

  /**
   * Given an Observable[RawJsonDocument], unmarshal an entity of type T from it
   * @param jsonDocument The document returned from couchbase to unmarshal the entity from
   * @tparam T The type of entity to unmarshal from the document
   * @return An entity of type T if successful
   */
  private def convertToEntity[T](jsonDocument: Observable[RawJsonDocument])
                                (implicit m: Marshaller[T, MessageEntity], unm: Unmarshaller[MessageEntity, T]): Future[Option[T]] = {
    val docPublisher = RxReactiveStreams.toPublisher(jsonDocument)
    val entityPromise = Promise[Option[T]]()
    val rawDocFuture = Source(docPublisher).runWith(Sink.head)
    rawDocFuture.onFailure {
      case ex: Exception => entityPromise.success(None)
    }
    rawDocFuture.onSuccess {
      case doc: RawJsonDocument => {
        val entity = HttpEntity(ContentTypes.`application/json`, doc.content())
        entityPromise.completeWith(unmarshalEntity[T](entity).map(Some(_)))
      }
    }
    entityPromise.future
  }

  private def convertToEntity[T](jsonDocument: RawJsonDocument)
                                (implicit m: Marshaller[T, MessageEntity], unm: Unmarshaller[MessageEntity, T]): Future[T] = {
    unmarshalEntity[T](HttpEntity(ContentTypes.`application/json`, jsonDocument.content()))
  }

  /**
   * Replace a document in couchbase with the given entity by marshalling it to
   * json, then returning the unmarshalled json returned from couchbase
   * @param entity Overwrite the document in the database with this entity
   * @param key The document to overwrite
   * @tparam T The type of the entity
   * @return The replaced document in the database
   */
  def replaceDocument[T](entity: T, key: String)
                        (implicit m: Marshaller[T, MessageEntity], unm: Unmarshaller[MessageEntity, T]): Future[Option[T]] = {
    marshalEntity[T](entity).flatMap {jsonString =>
      val doc = RawJsonDocument.create(key, jsonString)
      val insertObservable = bucket.async().upsert(doc)
      convertToEntity[T](insertObservable)
    }
  }


  /**
   * Marshall entity to json, insert it into the database, then unmarshal the response and return it
   * @param entity The object to insert
   * @param key The location to insert it to
   * @tparam T The type of the object
   * @return The resulting document after it has been inserted
   */
  def insertDocument[T](entity: T, key: String)
                       (implicit m: Marshaller[T, MessageEntity], unm: Unmarshaller[MessageEntity, T]): Future[Option[T]] = {
    marshalEntity[T](entity).flatMap {jsonString =>
      val doc = RawJsonDocument.create(key, jsonString)
      val insertObservable = bucket.async().insert(doc)
      convertToEntity[T](insertObservable)
    }
  }

  /**
   * Lookup a document in couchbase by the key, and unmarshal it into an object: T
   * @param key The key to lookup in the database
   * @tparam T The type to unmarshal the returned document to
   * @return A Future with the object T if found, otherwise None
   */
  def lookupByKey[T](key: String)(implicit m: Marshaller[T, MessageEntity], unm: Unmarshaller[MessageEntity, T]): Future[Option[T]] = {
    val lookupPromise = Promise[Option[T]]()

    val docObservable = bucket.async().get(key, classOf[RawJsonDocument])
    // todo: add exception handling if we can't find it by key
    val docEntity = convertToEntity[T](docObservable)(m, unm)
    docEntity.onComplete{
      case Success(t) => lookupPromise.complete(Success(t))
      case Failure(ex) => lookupPromise.failure(ex)
    }
    lookupPromise.future
  }

  /**
   * Retrieve a list of keys in Couchbase using the batch async system
   * @param keys The list of keys to retrieve
   * @return A Source of RawJsonDocuments
   */
  def batchLookupByKey(keys: List[String]): Source[RawJsonDocument, Any] = {
    import rx.lang.scala._
    val docObservable = Observable.from(keys)
      .flatMap(key => bucket.async().get(key, classOf[RawJsonDocument]))
    observableToSource(docObservable)
  }


  /**
   * Remove a document from the database
   * @param key The document to remove
   * @return A Successful future if the document was found, otherwise a Failure
   */
  def removeByKey(key: String): Future[Unit] = {
    val removedObservable = bucket.async().remove(key)
    val publisher = RxReactiveStreams.toPublisher(removedObservable)
    Source(publisher).runWith(Sink.ignore)
  }

  /**
   * Extract an entity of type T from an AsyncViewRow
   * @param row The AyncViewRow to extract the entity from
   * @tparam T The type of the entity to unmarshal from the row
   * @return The unmarshalled entity if successful
   */
  def getEntityFromRow[T](row: AsyncViewRow)
                         (implicit m: Marshaller[T, MessageEntity], unm: Unmarshaller[MessageEntity, T]): Future[Option[T]] = {
    convertToEntity[T](row.document(classOf[RawJsonDocument]))
  }

  /**
   * Query the designDoc/viewDoc for the given key, and return the resulting row if found
   * @param designDoc The design document containing the view
   * @param viewDoc The view to query
   * @param key The key to lookup in the query
   * @param forceIndex Set to true to force the view index to update on request. This should be used carefully
   * @return The row containing the key if found
   */
  def indexQuerySingleElement(designDoc: String, viewDoc: String, key: String, forceIndex: Boolean=false): Future[Option[AsyncViewRow]] = {
    val staleState = if (forceIndex) Stale.FALSE else Stale.TRUE

    // Query the view and convert the first row found into a future
    val query = ViewQuery.from(designDoc, viewDoc).stale(staleState).inclusiveEnd().key(key)
    val queryObservable = toScalaObservable(bucket.async().query(query))
    val rowObservable = queryObservable.flatMap(queryResult => queryResult.rows().first())
    val rowFuture = observableToFuture(rowObservable)

    // Check if a row was returned, and return None if it was not found else return the row
    val resultPromise = Promise[Option[AsyncViewRow]]()
    rowFuture.onComplete {
      case Success(row) => resultPromise.success(Some(row))
      case Failure(ex: NoSuchElementException) => resultPromise.success(None)
      case Failure(ex) => resultPromise.failure(ex)
    }
    resultPromise.future
  }

  /**
   * Query an index for multiple items
   * @param designDoc The design document name to query
   * @param viewDoc The view in that design doc
   * @param keys The list of keys to query for
   * @param stale Allow potentially stale indexes or not
   * @return Observable of AsyncViewRow
   */
  def indexQuery(designDoc: String, viewDoc: String, keys: List[String], stale: Stale = Stale.FALSE): Observable[AsyncViewRow] = {
    // Couchbase needs a java.util.List
    val keyList: java.util.List[String] = keys
    val query = ViewQuery.from(designDoc, viewDoc).stale(stale).inclusiveEnd(true).keys(JsonArray.from(keyList))
    toScalaObservable(bucket.async().query(query))
      .flatMap(queryResult => queryResult.rows())
  }

  /**
   * Query an index with a compound key
   * @param designDoc The name of the design document
   * @param viewDoc The name of the view
   * @param keys A List of lists to query for
   * @param stale Allow stale records
   * @return Observable of AsyncViewRows
   */
  def compoundIndexQuery(designDoc: String, viewDoc: String, keys: List[List[String]], stale: Stale = Stale.FALSE): Observable[AsyncViewRow] = {
    // Couchbase needs a java.util.List
    val keyList: java.util.List[java.util.List[String]] = keys.map(seqAsJavaList)
    val query = ViewQuery.from(designDoc, viewDoc).stale(stale).inclusiveEnd(true).keys(JsonArray.from(keyList))
    toScalaObservable(bucket.async().query(query))
      .flatMap(queryResult => queryResult.rows())
  }

  /**
   * Get the documents found from an Observable[AsyncViewRow]
   * @param docObservable An Observable[AsyncViewRow] as the list of documents
   * @return A new Observable[RawJsonDocument]
   */
  def withDocuments(docObservable: rx.lang.scala.Observable[AsyncViewRow]): Observable[RawJsonDocument] = {
    docObservable.flatMap(_.document(classOf[RawJsonDocument]))
  }


  /**
   * Query an index and unmarshal the found documents into an entity of type T
   * @param designDoc The name of the design document to query
   * @param viewDoc The name of the view
   * @param keys The keys to query for
   * @param stale Allow stale records or not
   * @tparam T The entity type to unmarshal to
   * @return A Source[T, Any] of the found documents.
   */
  def indexQueryToEntity[T](designDoc: String, viewDoc: String, keys: List[String], stale: Stale = Stale.FALSE)
                           (implicit m: Marshaller[T, MessageEntity], unm: Unmarshaller[MessageEntity, T]): Source[T, Any] = {
    val query = indexQuery(designDoc, viewDoc, keys, stale)
    val docs = withDocuments(query)
    Source(RxReactiveStreams.toPublisher(docs))
      // Unmarshal the json
      .map(convertToEntity[T])
      // convert resulting Future[T] to a regular Source[T, Any]
      .map(f => Source[T](f)).flatten(FlattenStrategy.concat)
  }

  /**
   * Query a compound index and unmarshal the found documents into an entity of type T
   * @param designDoc The name of the design document
   * @param viewDoc The name of the view
   * @param keys The list of keys to query for, where each compound key is a list of keys
   * @param stale Allow stale records or not
   * @tparam T The type of the entity to unmarshal to
   * @return A Source[T, Any] of the found documents.
   */
  def compoundIndexQueryToEntity[T](designDoc: String, viewDoc: String, keys: List[List[String]], stale: Stale = Stale.FALSE)
                                   (implicit m: Marshaller[T, MessageEntity], unm: Unmarshaller[MessageEntity, T]): Source[T, Any] = {
    val query = compoundIndexQuery(designDoc, viewDoc, keys, stale)
    val docs = withDocuments(query)
    Source(RxReactiveStreams.toPublisher(docs))
      // Unmarshal the json
      .map(convertToEntity[T])
      // convert resulting Future[T] to a regular Source[T, Any]
      .map(f => Source[T](f)).flatten(FlattenStrategy.concat)
  }

}