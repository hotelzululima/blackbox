package test

import play.api.mvc.Result
import org.scalatestplus.play._

import play.api.test.FakeRequest
import play.api.libs.json._
import play.api.Application
import play.api.test.Helpers._

import models._

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}


class BoxesControllerSpec extends PlaySpec with OneAppPerSuite with DBReset{

  //these are needed to execute 'call' on EssentialAction objects
  implicit val system = ActorSystem("Sys")
  implicit val mat: Materializer = ActorMaterializer()

  def fakeRequest(method: String = "GET", route: String = "/") = FakeRequest(method, route)
    .withHeaders(
      ("Date", "2014-10-05T22:00:00"),
      ("Authorization", "token=fd7ad598-84cb-11e5-a2d8-a7329ba812a2") // this be fake
    )

  def applicationController() = {
    val app2ApplicationController = Application.instanceCache[controllers.Application]
    app2ApplicationController(app)
  }

  def boxesController() = {
    val app2BoxesController = Application.instanceCache[controllers.BoxesController]
    app2BoxesController(app)
  }

  "BoxesController" should {
    "accept a valid json box object" in {

      val jsonBody = Json.parse(
        s"""  {
            |    "title": "Fake Box"
            |  }
       """.stripMargin)

      val request = fakeRequest("POST", "/boxes").withJsonBody(jsonBody)
      val response: Future[Result] = call(boxesController.createBoxes, request)
      status(response) must equal(CREATED)
    }

    "get a list of boxes" in {

      val request = fakeRequest("GET", "/boxes")
      val response: Future[Result] = call(boxesController.listBoxes, request)

      status(response) must equal(OK)
      contentType(response).get must equal("application/json")

      val boxesList = Json.parse(contentAsString(response)).as[List[Box]]

      boxesList.length >= 1

    }
  }
}
