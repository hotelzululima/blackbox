package models

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Date, UUID}
import play.api.libs.json._
import slick.driver.JdbcProfile

/**
  * Created by rroche on 11/3/15.
  */

/**
  * Models a Box.
  *
  * A Box is the main container for a support ticket and all its logs (Stories).
  *
  * @param id The unique id of the object.
  * @param title Title for the box.
  * @param created date when the box was created.
  * @param dronekitMission Unique id for the DroneKit-Cloud Mission object this Box belongs to.
  */
case class Box(id: UUID = java.util.UUID.randomUUID(),
               title: String,
               created: Timestamp = (new Timestamp(new Date().getTime())),
               dronekitMission: Option[UUID])

object Box {
  implicit val boxFormat = Json.format[Box]

  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }
}