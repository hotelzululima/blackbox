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
case class Box(id: Option[UUID] = Some(java.util.UUID.randomUUID()),
               title: String,
               created: Option[Timestamp] = Some(new Timestamp(new Date().getTime)),
               dronekitMission: Option[UUID] = None)

object Box {

  implicit object boxFormat extends Format[Box] {

    val bbDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS")

    def reads(json: JsValue) = JsSuccess(Box(
      title = (json \ "title").as[String]
    ))

    def writes(box: Box): JsValue = JsObject(Seq(
      "id" -> JsString(box.id match { case Some(id) => id.toString }),
      "title" -> JsString(box.title),
      "created" -> JsString(bbDateFormat.format(box.created match { case Some(created) => created } )),
      "dronekitMission" -> JsString(box.dronekitMission match { case Some(id) => id.toString })
    ))
  }

}