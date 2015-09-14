package com.dddr.blackbox.http

import com.dddr.blackbox.models._
import spray.json.{JsonFormat, JsString, JsValue, DefaultJsonProtocol}

/**
 * Created by rroche on 9/8/15.
 */
trait Protocol extends DefaultJsonProtocol {
  implicit object boxIdFormat extends JsonFormat[BoxId] {
    override def write(obj: BoxId): JsValue = JsString(obj.id)
    override def read(json: JsValue): BoxId = BoxId(json.convertTo[String])
  }
  implicit object userIdFormat extends JsonFormat[UserId] {
    override def write(obj: UserId): JsValue = JsString(obj.id)
    override def read(json: JsValue): UserId = UserId(json.convertTo[String])
  }
  implicit val softwareVersionFormat = jsonFormat2(SoftwareVersion)
  implicit val boxFormat = jsonFormat7(BoxEntity)
  implicit val boxFormatNew = jsonFormat4(BoxEntityNew)
  implicit val userFormat = jsonFormat4(UserEntity)
}
