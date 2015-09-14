package com.dddr.blackbox.utils

import java.util.NoSuchElementException

import com.couchbase.client.java.document.JsonDocument
import com.couchbase.client.java.document.json.JsonObject
import com.couchbase.client.java.view.{DefaultView, DesignDocument}
import rx.lang.scala.JavaConversions.toScalaObservable

import scala.collection.JavaConversions._

/**
 * Created by rroche and this other guy again on 8/6/15.
 *
 * Couchbase schema migrations
 */
trait CouchbaseMigration extends CouchbaseSupport {

  /**
   * Migrate from whatever the current version of the database is to the most recent
   * defined here
   */
  val versionDocumentKey = "schemaVersion"

  def migrate(): Unit = {
    log.info("Migrating couchbase")
    val versionObservable = bucket.async().get(versionDocumentKey)
    // Make blocking so migration is completed before we return and start application
    try {
      val doc = toScalaObservable(versionObservable).toBlocking.first
      upgradeFromVersion(Some(doc.content().getInt("version").toInt))
    } catch {
      case ex: NoSuchElementException => {
        log.info("Did not find version document")
        upgradeFromVersion(None)
      }
    }
  }

  /**
   * Upgrade to the current version from the current version
   * @param version The current version of the schema
   */
  def upgradeFromVersion(version: Option[Int]): Unit = {
    log.info(s"upgrading from version $version")
    version match {
      case None => initializeSchema()
      case Some(v) => v match {
        case 1 => upgradeFrom1()
      }
    }
  }

  /**
   * Create the initial couchbase schema, including any required documents and views
   */
  def initializeSchema(): Unit = {
    log.info("Creating initial couchbase schema")

    // Users View, to query user documents by login
    val usersMap =
      """
        |function (doc, meta) {
        |  if (doc.docType && doc.docType === "user") {
        |    emit(doc.email);
        |  }
        |}
      """.stripMargin
    val usersView = DefaultView.create("users", usersMap)
    val usersDesignDoc = DesignDocument.create("users", List(usersView))
    bucket.bucketManager().upsertDesignDocument(usersDesignDoc, false)

    val boxMap =
      """
        |function(doc, meta) {
        | if (doc.docType && doc.docType === "box") {
        |   emit(doc.userId);
        | }
        |}
      """.stripMargin
    val boxView = DefaultView.create("boxes", usersMap)
    val boxesDesignDoc = DesignDocument.create("boxes", List(boxView))
    bucket.bucketManager().upsertDesignDocument(boxesDesignDoc, false)

    // Version document, used by subsequent migrations
    val versionDocument = JsonDocument.create(versionDocumentKey, JsonObject.empty.put("version", 1))
    bucket.insert(versionDocument)
  }

  /**
   * Upgrade from the initial version to version 2.
   */
  def upgradeFrom1(): Unit = {
    // Placeholder for now
  }
}