package controllers

import javax.inject.Inject
import org._3dr.blackbox.dal.UserRepository
import org._3dr.blackbox.services.ZendeskAuthService
import play.api.libs.json.{Json, JsError, JsPath, Reads}
import play.api.libs.ws.WSClient
import play.api.mvc.{BodyParsers, Action, Controller}

import scala.concurrent.{Future, ExecutionContext}
import play.api.libs.functional.syntax._
/**
  * Created by genarorg on 11/19/15.
  */
class AuthController  @Inject() (db: UserRepository, ws: WSClient)
                                (implicit ec: ExecutionContext) extends Controller with ZendeskAuthService{

  //used to validate incoming JSON payload for logging in
  private val loginForm: Reads[(String, String)] =
    (JsPath \ "email").read[String] and
    (JsPath \ "password").read[String] tupled

  def login = Action.async(BodyParsers.parse.json) { implicit request =>

    request.body.validate(loginForm).fold(
      errors => {
        Future(BadRequest(JsError.toJson(errors)))
      },
      form => {
        Future(Ok("all right man!"))
      }
    )
  }
}
