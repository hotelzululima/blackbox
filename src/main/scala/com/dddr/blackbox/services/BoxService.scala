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
  /*
  def createMission(newMission: NewMissionEntity, user: UserEntity): Future[Option[MissionEntity]] = {
    val missionPromise = Promise[Option[MissionEntity]]()
    val mission = MissionEntity(name = newMission.name, vehicleId = newMission.vehicleId,
      actions = newMission.actions.getOrElse(List()),
      tags = newMission.tags.getOrElse(List()),
      owners = Some(user.toOwnerIdentifier))

    // Verify this vehicle ID exists
    val vehicleFuture = lookupByKey[VehicleEntity](newMission.vehicleId.key)
    vehicleFuture.onComplete {
      case Success(vehicleOpt: Some[VehicleEntity]) =>
        missionPromise.completeWith(insertDocument[MissionEntity](mission, mission.id.key))
      case Success(None) => println(s"Failed to find vehicle with key: ${newMission.vehicleId.key}"); missionPromise.success(None)
      case Failure(ex) => missionPromise.failure(ex)
    }
    missionPromise.future
  }
   */
//  def getMissionById(missionId: MissionId): Future[Option[MissionEntity]] = {
//    lookupByKey[MissionEntity](missionId.key)
//  }
//  def saveBox(implicit box: BoxEntity): Future[Option[BoxEntity.type]] = {
//    insertDocument(BoxEntity, BoxId().key)
//  }
}
