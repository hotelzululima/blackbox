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


class AuthControllerSpec extends DBReset{

  //these are needed to execute 'call' on EssentialAction objects
  implicit val system = ActorSystem("Sys")
  implicit val mat: Materializer = ActorMaterializer()

  def authController() = {
    val app2AuthController = Application.instanceCache[controllers.AuthController]
    app2AuthController(app)
  }

  "Zendesk Authorization Controller" should {

    "Accept and validate a json request" in {

      val correctJsonBody = Json.obj("email" -> "test@tesrt.com", "password" -> "testpassword")
      val incorrectJsonBody = Json.obj("invalidkey1" -> "test@tesrt.com")

      var request = FakeRequest("POST", "/api/v1/login").withJsonBody(correctJsonBody)
      var response: Future[Result] = call(authController.login, request)

      //validate correct request. Fake credentials should return 401
      status(response) must equal(UNAUTHORIZED)

      request = FakeRequest("POST", "/api/v1/login").withJsonBody(incorrectJsonBody)
      response = call(authController.login, request)

      //validate incorrect request. Should return 400
      status(response) must equal(BAD_REQUEST)

    }

    "Validate agent credentials on Zendesk" in {

      var jsonBody = Json.obj("email" -> "genaro.rocha@3drobotics.com", "password" -> "Rurounnii2!")
      var request = FakeRequest("POST", "/api/v1/login").withJsonBody(jsonBody)
      var response: Future[Result] = call(authController.login, request)
      status(response) must equal(OK)

      jsonBody = Json.obj("email" -> "genaro.rochaz@3fake.com", "password" -> "123")
      request = FakeRequest("POST", "/api/v1/login").withJsonBody(jsonBody)
      response = call(authController.login, request)
      status(response) must equal(UNAUTHORIZED)

    }

  }
}
