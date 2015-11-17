package test

import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class ApplicationSpec extends PlaySpec with OneAppPerSuite{

  "Application" should {

    "send 404 on a bad request" in {
      val request = route(FakeRequest(GET, "/boum")).get
      status(request) must equal(NOT_FOUND)
    }

    "render the index page" in {
      val home = route(FakeRequest(GET, "/")).get
      status(home) must equal(OK)
      contentType(home).get must equal("text/html")
    }


  }

}