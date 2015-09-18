package com.dddr.blackbox.services

import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.dddr.blackbox.models._
import com.dddr.blackbox.utils.Config

import scala.concurrent.{Promise, Future}

/**
 * Created by rroche on 9/16/15.
 */
trait MediaService extends BaseService with Config {
  import protocol._
  import couchbase._

  def getAllMedia(boxId: BoxId): Future[Seq[MediaEntity]] = {
    val mediaSource = indexQueryToEntity[MediaEntity]("media", "media", List("boxId"))
    // TODO: Add pagination
    mediaSource.grouped(1000).runWith(Sink.head)
  }

  def createMediaNoStream(boxId: BoxId, data: ByteString, contentType: String): Future[Option[MediaEntity]] = {
    val p = Promise[Option[MediaEntity]]()
    val mediaType = ConvertMediaType(contentType).getMedia
    // find mission
    lookupByKey[BoxEntity](boxId.key).map {
      case Some(box) => {
        // add media to mission
        val mediaEntity = MediaEntity(boxId = box.id, contentType = mediaType)
        // insert to DKC
      }
      case None => p.success(None)
      case _ => p.failure(_)
    }
    p.future
  }

  def getMedia(mediaId: MediaId): Future[Option[MediaEntity]] = {
    lookupByKey[MediaEntity](mediaId.key)
  }

  def deleteMedia(mediaId: MediaId): Future[Unit] = {
    removeByKey(mediaId.key)
  }
}
