package com.dddr.blackbox.services

import akka.stream.scaladsl.{Source, Sink}
import com.dddr.blackbox.models._
import com.dddr.blackbox.utils.CouchbaseSupport

import scala.concurrent.{Promise, Future}

/**
 * Created by rroche on 9/8/15.
 */
trait BoxService extends CouchbaseSupport {
  def getListOfBoxes(): Future[Seq[BoxEntity]] = {
    indexQueryToEntity[BoxEntity]("boxes", "boxes", List()).grouped(1000).runWith(Sink.head)
  }
  def getBoxById(boxId: BoxId): Future[Option[BoxEntity]] = {
    lookupByKey[BoxEntity](boxId.key)
  }
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
