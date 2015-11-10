package test

import java.util.UUID

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.Application

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.specs2.mutable.Specification

import org._3dr.blackbox._
import play.api.test.WithApplication

/**
  * Created by genarorg on 11/9/15.
  */
@RunWith(classOf[JUnitRunner])
class ModelSpec extends Specification {

  import models._

  // -- Date helpers

  def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str

  // --

  "Box Repository" should {

    def bbRepo(implicit app: Application) = {
      val app2BoxRepository = Application.instanceCache[BoxRepository]
      app2BoxRepository(app)
    }

    val dronekitMissionId: UUID = UUID.fromString("4e536d2c-2cbb-4475-a07e-ce7776893f03")
    val boxTitle = "A test box from spec"

    "create a new box and retrieve it" in new WithApplication {

      val insertedBox: Box = Await.result(bbRepo.createBox(boxTitle, Some(dronekitMissionId)), Duration.Inf)
      insertedBox.title must equalTo(boxTitle)
      insertedBox.dronekitMission must be equalTo Some(dronekitMissionId)

    }

    "return all boxes" in new WithApplication {
      //insert one box
      val insertedBox: Box = Await.result(bbRepo.createBox(boxTitle, Some(dronekitMissionId)), Duration.Inf)
      //get all, check if the last one is the same
      val boxes: Seq[Box] = Await.result(bbRepo.listBoxes(), Duration.Inf)
      boxes.last should equalTo(insertedBox)

    }

    "find a box by ID" in new WithApplication {
      //insert one box
      val insertedBox: Box = Await.result(bbRepo.createBox(boxTitle, Some(dronekitMissionId)), Duration.Inf)
      //get the same box by id
      val box: Box = Await.result(bbRepo.findBoxById(insertedBox.id.get), Duration.Inf).get
      box should equalTo(insertedBox)

    }

  }

}

