package test

import javax.inject.Inject

import org._3dr.blackbox._

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import scala.concurrent.ExecutionContext

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.Application

/**
  * Created by rroche on 11/6/15.
  */

@RunWith(classOf[JUnitRunner])
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
    val app2ApplicationController = Application.instanceCache[controllers.BoxesController]
    app2ApplicationController(app)
  }

  "BoxesController" should {
    "accept a valid json box object" in new WithApplication {
      val request = FakeRequest().withJsonBody(Json.parse(
      s"""  {
         |    "title": "Fake Box"
         |  }
       """.stripMargin))
      println("fuck yeah tests!")
      println(request)
      val response = boxesController.createBoxes(request)
      println(response)
      status(response.) must equalTo(CREATED)
      // val jsonResponse = contentAsJson(response)
      // ObjectId.isValid((jsonResponse \ "id").as[String]) mustBe true
    }
  }
}
