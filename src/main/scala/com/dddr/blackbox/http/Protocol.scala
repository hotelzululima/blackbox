package com.dddr.blackbox.http

import com.dddr.blackbox.models._
import spray.json._

/**
 * Created by rroche on 9/8/15.
 */
class Protocol extends DefaultJsonProtocol {
  implicit object mediaTypeFormat extends RootJsonFormat[MediaType] {
    def write(obj: MediaType) = JsString(obj.media)

    def read(value: JsValue) = value match {
      case JsString(str) => {
        if (str.contains("text")) {
          TextMedia()
        } else if (str.contains("image")) {
          ImageMedia()
        } else if (str.contains("video")) {
          VideoMedia()
        } else {
          BinaryMedia()
        }
      }
      case _ => throw new DeserializationException("Cannot deserialize media type")
    }
  }
  implicit object boxIdFormat extends JsonFormat[BoxId] {
    override def write(obj: BoxId): JsValue = JsString(obj.id)
    override def read(json: JsValue): BoxId = BoxId(json.convertTo[String])
  }
  implicit object userIdFormat extends JsonFormat[UserId] {
    override def write(obj: UserId): JsValue = JsString(obj.id)
    override def read(json: JsValue): UserId = UserId(json.convertTo[String])
  }
  implicit object mediaIdFormat extends JsonFormat[MediaId] {
    override def write(obj: MediaId): JsValue = JsString(obj.id)
    override def read(json: JsValue): MediaId = MediaId(json.convertTo[String])
  }
  implicit val softwareVersionFormat = jsonFormat2(SoftwareVersion)
  implicit val boxFormat = jsonFormat7(BoxEntity)
  implicit val boxFormatNew = jsonFormat5(BoxEntityNew)
  implicit val userFormat = jsonFormat4(UserEntity)
  implicit val mediaFormat = jsonFormat4(MediaEntity)
}
