package controllers

import javax.inject._

import org._3dr.blackbox._
import play.api.mvc._

import models._
import play.api.libs.json.{JsError, Json}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * Created by rroche on 11/6/15.
  */
class BoxesController @Inject() (db: BoxRepository)
                                (implicit ec: ExecutionContext) extends Controller {

  def createBoxes = Action(parse.json) { request =>
    request.body.validate[Box].map{
      case box => Created(Json.toJson(box))
    }.recoverTotal {
      e => BadRequest("Validation Error:"+ JsError.toJson(e))
    }
  }
}
