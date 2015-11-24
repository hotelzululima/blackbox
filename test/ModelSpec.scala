package test

import java.util.UUID
import org._3dr.blackbox.dal.{BoxRepository}
import play.api.Application

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org._3dr.blackbox._
import models._
import org.scalatestplus.play._


class ModelSpec extends DBReset{

  "Box Repository" must {

    def bbRepo(implicit app: Application) = {
      val app2BoxRepository = Application.instanceCache[BoxRepository]
      app2BoxRepository(app)
    }

    val dronekitMissionId: UUID = UUID.fromString("4e536d2c-2cbb-4475-a07e-ce7776893f03")
    val boxTitle = "A test box from spec"

    "create a new box and retrieve it" in {

      val insertedBox: Box = Await.result(bbRepo.createBox(boxTitle, Some(dronekitMissionId)), Duration.Inf)
      insertedBox.title must equal(boxTitle)
      insertedBox.dronekitMission must equal(Some(dronekitMissionId))

    }

    "return all boxes" in {
      //insert one box
      val insertedBox: Box = Await.result(bbRepo.createBox(boxTitle, Some(dronekitMissionId)), Duration.Inf)
      //get all, check if the last one is the same
      val boxes: Seq[Box] = Await.result(bbRepo.listBoxes, Duration.Inf)
      boxes.last must equal(insertedBox)

    }

    "find a box by ID" in {
      //insert one box
      val insertedBox: Box = Await.result(bbRepo.createBox(boxTitle, Some(dronekitMissionId)), Duration.Inf)
      //get the same box by id
      val box: Box = Await.result(bbRepo.findBoxById(insertedBox.id), Duration.Inf).get
      box must equal(insertedBox)

    }
  }
}

