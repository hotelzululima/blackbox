package models

import java.util.UUID

import play.api.libs.json.Json

/**
  * Created by genarorg on 11/19/15.
  * These attributes map diretly to Zendesk user attributes
  */
case class User(id: Long, name: String, email: String, role: String, active: Boolean)

object User{
  implicit val userReads = Json.reads[User]
  implicit val userWrites = Json.writes[User]
}