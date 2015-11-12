package controllers

import java.util.UUID
import javax.inject._

import org._3dr.blackbox._
import play.api.mvc._

import models._
import play.api.libs.json.{JsError, Json}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Created by rroche on 11/6/15.
  */
class VehiclesController @Inject() (db: BoxRepository)
                                (implicit ec: ExecutionContext) extends Controller {

  def createVehicles = Action.async(BodyParsers.parse.json) { implicit request =>

    val vehicle = request.body.validate[NewVehicle]
    vehicle.fold(
      errors => {
        Future(BadRequest("Validation Error:"+ JsError.toJson(errors)))
      },
      vehicle => {
        db.createVehicle(vehicle.mac, Some(UUID.randomUUID())).map(vehicle => Created(Json.toJson(vehicle)))
      }
    )
  }

  def listVehicles = Action.async { implicit request =>
    db.listVehicles.map(vehicles => Ok(Json.toJson(vehicles)))
  }
}
