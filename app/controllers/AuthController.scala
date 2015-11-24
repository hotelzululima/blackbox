package controllers


import java.util.concurrent.TimeoutException
import javax.inject.Inject
import org._3dr.blackbox.services.ZendeskService
import org._3dr.blackbox.utils.{Config, ValidationErrorFormat, TokenManager}
import play.api.libs.json.{Json, JsPath, Reads}
import play.api.libs.ws.{WSResponse, WSClient}
import play.api.mvc.{BodyParsers, Action, Controller, Result}

import scala.concurrent.{Future, ExecutionContext}
import play.api.libs.functional.syntax._
import models._

import authentikat.jwt._

/**
  * Created by genarorg on 11/19/15.
  */
class AuthController  @Inject() (implicit ec: ExecutionContext, implicit val ws:WSClient) extends Controller with ZendeskService with ValidationErrorFormat {


  def login = Action.async(BodyParsers.parse.json) { implicit request =>

    request.body.validate(loginForm).fold(
      errors => {
        Future(BadRequest(Json.toJson(errors)))
      },
      form => {
        ZendeskResponseHandler(getUser(email = form._1, password = form._2), onZendeskResponse)
      }
    )
  }

  def onZendeskResponse(response: WSResponse) : Result = {
    (response.json \ "user").validate[User].fold(
      errors => { Unauthorized(Json.obj("error" -> Json.toJson(ZendeskLoginError)))},
      user => {
        if(user.role != "end-user") {
          val jwtToken: String = TokenManager.createJWT[User](user)
          Ok(Json.obj("user"->Json.toJson(user)))
            .withHeaders(Config.JWTHeaderName.toString -> (Config.JWTPrefix + jwtToken))
        }else{
          Unauthorized(Json.obj("error" -> Json.toJson(ZendeskLoginError)))
        }
      }
    )
  }

  //check for requests errors, if the future evaluates correctly then execute the callback function
  def ZendeskResponseHandler(response: Future[WSResponse], callback: (WSResponse) => Result) = {
    response map {
      response =>
        callback(response)
    } recover {
      case t: TimeoutException =>
        RequestTimeout(Json.obj("error" -> Json.toJson(ZendeskConnectionError)))
      case e =>
        ServiceUnavailable(Json.obj("error" -> Json.toJson(ZendeskConnectionError)))
    }
  }

  //used to validate incoming JSON payload for logging in
  private val loginForm: Reads[(String, String)] =
    (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String] tupled


}
