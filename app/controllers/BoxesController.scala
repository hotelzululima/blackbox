package controllers

import java.util.UUID
import javax.inject._

import org._3dr.blackbox._
import play.api.mvc._

import models._
import play.api.libs.json.{JsError, Json}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Created by rroche on 11/6/15.
  */
class BoxesController @Inject() (db: BoxRepository)
                                (implicit ec: ExecutionContext) extends Controller {

  def createBoxes = Action.async(BodyParsers.parse.json) { implicit request =>

    val box = request.body.validate[NewBox]
    box.fold(
      errors => {
        Future(BadRequest("Validation Error:"+ JsError.toJson(errors)))
      },
      box => {
        db.createBox(box.title, Some(UUID.randomUUID())).map(box => Created(Json.toJson(box)))
      }
    )
  }

  def listBoxes = Action.async { implicit request =>
   db.listBoxes.map(boxes => Ok(Json.toJson(boxes)))
  }
}
