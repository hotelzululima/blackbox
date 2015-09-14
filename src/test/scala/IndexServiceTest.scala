import akka.http.scaladsl.model.StatusCodes._

/**
 * Created by rroche on 9/8/15.
 */
class IndexServiceTest extends BaseServiceTest {
  it should "respond with 200 status" in {
    Get("/") ~> routes ~> check {
      response.status shouldBe OK
    }
  }
}
