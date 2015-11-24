package models

import play.api.libs.json.{Reads, Writes, Json}

case class Error(code:Int, title: String, details: String)

object Error{
  implicit val errorReads: Reads[Error] = Json.reads[Error]
  implicit val errorWrites: Writes[Error] = Json.writes[Error]
}

object ZendeskConnectionError extends Error(201, "Zendesk Server could not be reached", "An error occurred while trying to access the zendesk server")
object ZendeskLoginError extends Error(202, "Could not login to Zendesk", "Could not login to zendesk using the credentials provided")



