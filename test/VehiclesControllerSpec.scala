package test

import models.Vehicle
import play.api.Application
import play.api.libs.json.Json
import play.api.test.WithApplication
import utils.TestUtil

/**
  * Created by rroche on 11/12/15.
  */
class VehiclesControllerSpec extends TestUtil{
  def vehiclesController(implicit app: Application) = {
    val app2VehiclesController = Application.instanceCache[controllers.VehiclesController]
    app2VehiclesController(app)
  }

  "VehiclesController" should {
    "createVehicles" in new WithApplication {
      val jsonBody = Json.parse(
      s""" {
         | "mac": "3c:15:c2:e5:fe:c0"
         | }
       """.stripMargin)

      val request = fakeRequest("POST", "/vehicles").withJsonBody(jsonBody)
      val response = call(vehiclesController.createVehicles, request)
      status(response) must equalTo(CREATED)
    }

    "listVehicles" in new WithApplication() {
      val request = fakeRequest("GET", "/vehicles")
      val response = call(vehiclesController.listVehicles, request)

      status(response) must equalTo(OK)
      contentType(response) must beSome("application/json")

      val vehiclesList = Json.parse(contentAsString(response)).as[List[Vehicle]]

      vehiclesList.length must beGreaterThanOrEqualTo(1)
    }
  }

}
