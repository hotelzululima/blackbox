package test

import play.api.test.PlaySpecification
import play.api.test.WithApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.Application
import scala.concurrent._

import org.specs2._

/**
  * Created by rroche on 11/6/15.
  */

class BoxesControllerSpec extends PlaySpecification {

  def fakeRequest(method: String = "GET", route: String = "/") = FakeRequest(method, route)
    .withHeaders(
      ("Date", "2014-10-05T22:00:00"),
      ("Authorization", "token=fd7ad598-84cb-11e5-a2d8-a7329ba812a2") // this be fake
    )

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

      val request = fakeRequest("POST", "/").withJsonBody(jsonBody)
      val response = call(boxesController.createBoxes, request)
      status(response) must equalTo(CREATED) //yessssss

    }
  }
}
