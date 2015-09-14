import akka.http.scaladsl.model.{HttpEntity, ContentTypes}
import com.dddr.blackbox.models.{BoxId, BoxEntity, UserId}
import akka.http.scaladsl.model.StatusCodes._

/**
 * Created by rroche on 9/8/15.
 */
class BoxServiceTest extends BaseServiceTest {
  it should "get a list of boxes" in {
    Get("/box") ~> routes ~> check {
      response.status shouldBe OK
      val data = responseAs[List[BoxEntity]]
      data.length should be > 0
    }
  }
  it should "save a mission on Post and be able to retrieve it later" in {
    /*
    val newMission = s"""{"name": "Flying High", "vehicleId": "${testVehicles(0).id}"} """
      val entity = HttpEntity(ContentTypes.`application/json`, newMission)
      var missionId = MissionId()
      Post("/missions", entity) ~> addHeader("Token", testTokens.head.token) ~> missionsRoute ~> check {
        val returnedMission = responseAs[MissionEntity]
        response.status shouldBe Created
        returnedMission.name shouldBe "Flying High"
        returnedMission.vehicleId shouldBe testVehicles(0).id
        missionId = returnedMission.id
      }
     */
    val newBox = s"""{"title": "Sample", "description": "Sample", "firmware": [], "category": [], "userId": 1 } """
    val entity = HttpEntity(ContentTypes.`application/json`, newBox)
    var newBoxId = BoxId()
    Post("/box", entity) ~> routes ~> check {
      val returnedBox = responseAs[BoxEntity]
      response.status shouldBe Created
      returnedBox.title shouldBe "Sample"
      returnedBox.description shouldBe "Sample"
      newBoxId = returnedBox.id
    }

    Get(s"/box/${newBoxId.id}") ~> routes ~> check {
      val returnedBox = responseAs[BoxEntity]
      response.status shouldBe OK
      returnedBox.title shouldBe "lame box"
      returnedBox.description shouldBe "lame description"
    }
  }
  /*
  Get(s"/missions/${missionId.id}") ~> addHeader("Token", testTokens.head.token) ~> missionsRoute ~> check {
        val returnedMission = responseAs[MissionEntity]
        response.status shouldBe OK
        returnedMission.name shouldBe "Flying High"
        returnedMission.id shouldBe missionId
        returnedMission.vehicleId shouldBe testVehicles(0).id
      }
   */
//  it should "save a box entity" in {
//    val sample_box = {
//      BoxEntity(title = "Sample", description = "Sample", category = List(), firmware = List(), userId = UserId())
//    }
//    Post("/box", sample_box) ~> routes ~> check {
//      response.status shouldBe OK
//      val data = responseAs[BoxEntity]
//      data.title shouldBe "Sample"
//      data.description shouldBe "Sample"
//    }
//  }
}
