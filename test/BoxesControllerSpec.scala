package test

import _root_.utils.TestUtil
import play.api.test.PlaySpecification
import play.api.test.WithApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc._
import play.api.libs.json._
import play.api.Application
import scala.concurrent._

import models._

import org.specs2._

/**
  * Created by rroche on 11/6/15.
  */

class BoxesControllerSpec extends TestUtil {

  def applicationController(implicit app: Application) = {
    val app2ApplicationController = Application.instanceCache[controllers.Application]
    app2ApplicationController(app)
  }

  def boxesController(implicit app: Application) = {
    val app2BoxesController = Application.instanceCache[controllers.BoxesController]
    app2BoxesController(app)
  }

  "BoxesController" should {
    "accept a valid json box object" in new WithApplication {

      val jsonBody = Json.parse(
        s"""  {
            |    "title": "Fake Box"
            |  }
       """.stripMargin)

      val request = fakeRequest("POST", "/boxes").withJsonBody(jsonBody)
      val response = call(boxesController.createBoxes, request)
      status(response) must equalTo(CREATED)
    }

    "get a list of boxes" in new WithApplication {
      val request = fakeRequest("GET", "/boxes")
      val response = call(boxesController.listBoxes, request)

      status(response) must equalTo(OK)
      contentType(response) must beSome("application/json")

      val boxesList = Json.parse(contentAsString(response)).as[List[Box]]

      boxesList.length must beGreaterThanOrEqualTo(1)
    }
  }
}
