package com.dddr.blackbox.services

import com.dddr.blackbox.models._
import com.dddr.blackbox.utils.CouchbaseSupport
import scala.util.{Failure, Success}

import scala.concurrent.{Promise, Future}

/**
 * Created by rroche on 9/8/15.
 */
trait BoxService extends CouchbaseSupport {
  def getListOfBoxes(): List[BoxEntity] = {
    val boxes = 1 to 5 map { num =>
      BoxEntity(title = "lame box", description = "lame description", category = List(), firmware = List(), userId = UserId("f5f6540e-cd67-4a2a-afdc-5141fa729a04"))
    }
    boxes.toList
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
