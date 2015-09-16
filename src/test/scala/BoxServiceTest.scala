import akka.http.scaladsl.model.{HttpEntity, ContentTypes}
import com.dddr.blackbox.models.{BoxId, BoxEntity, UserId}
import akka.http.scaladsl.model.StatusCodes._

/**
 * Created by rroche on 9/8/15.
 */
class BoxServiceTest extends BaseServiceTest {
  import protocol._
  val apiNamespace = "/api/v1"
  it should "get a list of boxes" in {
    Get(s"$apiNamespace/box") ~> routes ~> check {
      response.status shouldBe OK
      val data = responseAs[List[BoxEntity]]
      data.length should be > 0
    }
  }

  var newBoxId = BoxId()
  it should "save a mission on Post" in {
    val newBox = s"""{"title": "Sample", "description": "Sample", "firmware": [], "category": [], "userId": "1" } """
    val entity = HttpEntity(ContentTypes.`application/json`, newBox)

    Post(s"$apiNamespace/box", entity) ~> routes ~> check {
      val returnedBox = responseAs[BoxEntity]
      response.status shouldBe Created
      returnedBox.title shouldBe "Sample"
      returnedBox.description shouldBe "Sample"
      newBoxId = returnedBox.id
    }
  }
  it should "be able to get a mission by id" in {
    Get(s"$apiNamespace/box/${newBoxId.id}") ~> routes ~> check {
      val returnedBox = responseAs[BoxEntity]
      response.status shouldBe OK
      returnedBox.title shouldBe "Sample"
      returnedBox.description shouldBe "Sample"
    }
  }
}
