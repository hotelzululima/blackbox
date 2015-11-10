package models

import java.sql.Timestamp
import java.text.{DateFormat, SimpleDateFormat}
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
               created: Timestamp = (new Timestamp(new Date().getTime)),
               dronekitMission: Option[UUID] = None)

object Box {

  implicit object boxFormat extends Format[Box] {

    val bbDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS")

    def bbStringToTimestamp(date: String): Timestamp = {
      new Timestamp(bbDateFormat.parse(date).getTime())
    }

    def reads(json: JsValue) = JsSuccess(Box(
      id = (json \ "id").as[UUID],
      title = (json \ "title").as[String],
      created = bbStringToTimestamp((json \ "created").as[String]),
      dronekitMission = Option((json \ "dronekitMission").as[UUID])
    ))

    def writes(box: Box): JsValue = JsObject(Seq(
      "id" -> JsString(box.id.toString),
      "title" -> JsString(box.title),
      "created" -> JsString(bbDateFormat.format(box.created)),
      "dronekitMission" -> JsString(box.dronekitMission.getOrElse("").toString())
    ))
  }
}

/**
  * Validates a NewBox
  *
  * A Box is the main container for a support ticket and all its logs (Stories).
  *
  * @param title Title for the box.
  */
case class NewBox(title: String)

object NewBox {

  implicit object newBoxFormat extends Format[NewBox] {

    def reads(json: JsValue) = JsSuccess(NewBox(
      title = (json \ "title").as[String]
    ))

    def writes(box: NewBox): JsValue = JsObject(Seq(
      "title" -> JsString(box.title)
    ))
  }
}