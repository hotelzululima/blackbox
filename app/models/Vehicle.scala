package models

import java.sql.Timestamp
import java.util.{Date, UUID}

import models.utils.ModelUtil
import play.api.libs.json._

/**
  * Created by rroche on 11/12/15.
  */

case class Vehicle(id: UUID = UUID.randomUUID(),
                   mac: Option[String],
                   created: Timestamp = (new Timestamp(new Date().getTime)),
                   dronekitVehicle: Option[UUID])

object Vehicle {

  implicit object vehicleFormat extends Format[Vehicle] with ModelUtil {

    def reads(json: JsValue) = JsSuccess(Vehicle(
      id = (json \ "id").as[UUID],
      mac = Option((json \ "mac").as[String]),
      created = bbStringToTimestamp((json \ "created").as[String]),
      dronekitVehicle = Option((json \ "dronekitVehicle").as[UUID])
    ))

    def writes(vehicle: Vehicle): JsValue = JsObject(Seq(
      "id" -> JsString(vehicle.id.toString),
      "mac" -> JsString(vehicle.mac.getOrElse("").toString),
      "created" -> JsString(bbDateFormat.format(vehicle.created)),
      "dronekitVehicle" -> JsString(vehicle.dronekitVehicle.getOrElse("").toString)
    ))
  }
}

case class NewVehicle(mac: Option[String])

object NewVehicle {
  implicit object newVehicleFormat extends Format[NewVehicle] {
    def reads(json: JsValue) = JsSuccess(NewVehicle(
      mac = Option((json \ "mac").as[String])
    ))

    def writes(vehicle: NewVehicle): JsValue = JsObject(Seq(
      "mac" -> JsString(vehicle.mac.getOrElse("").toString)
    ))
  }
}