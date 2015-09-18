package com.dddr.blackbox.services

import java.util.UUID

import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.dddr.blackbox.models._

import scala.concurrent.{Future, Promise}

/**
 * Created by rroche on 9/8/15.
 */
trait BoxService extends BaseService {
  import couchbase._
  import protocol._

  def getListOfBoxes(): Future[Seq[BoxEntity]] = {
    indexQueryToEntity[BoxEntity]("boxes", "boxes").grouped(1000).runWith(Sink.head)
  }
  def getBoxById(boxId: BoxId): Future[Option[BoxEntity]] = {
    lookupByKey[BoxEntity](boxId.key)
  }
//  def createdMediaNoStream(boxId: BoxId, data: ByteString): MediaEntity = {
//    lookupByKey[BoxEntity](boxId.key).map {
//      case Some(box) => {
////        val uploadedMedia = dronekit.uploadMedia(missionId, data)
////        val mediaEntity = MediaEntity(boxId = box.id, mediaId = uploadedMedia.id, contentType = uploadedMedia.contentType)
//        //(id: MediaId = MediaId(), boxId: BoxId, mediaId: UUID = UUID.randomUUID(), contentType: String)
//        return MediaEntity(boxId = box.id, mediaId = UUID.randomUUID().toString(), contentType = "text")
//      }
//    }
//  }
  def createBox(newBox: BoxEntityNew): Future[Option[BoxEntity]] = {
    val boxPromise = Promise[Option[BoxEntity]]()
    val box = {
      BoxEntity(title = newBox.title,
        description = newBox.description,
        category = newBox.category.getOrElse(List()),
        firmware = newBox.firmware.getOrElse(List()),
        userId = UserId())
    }
    boxPromise.completeWith(insertDocument[BoxEntity](box, box.id.key))
    boxPromise.future
  }
}
