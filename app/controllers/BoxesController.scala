package controllers

import java.util.UUID
import javax.inject._

import org._3dr.blackbox.dal.BoxRepository
import org._3dr.blackbox.utils.ValidationErrorFormat
import play.api.mvc._

import play.api.libs.json.{JsPath, Reads, Json}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
  * Created by rroche on 11/6/15.
  */
class BoxesController @Inject() (db: BoxRepository)
                                (implicit ec: ExecutionContext) extends Controller with ValidationErrorFormat {

  //used to validate incoming JSON payload for new box
  private val createForm: Reads[(String)] = (JsPath \ "title").read[String]

  def createBoxes = Action.async(BodyParsers.parse.json) { implicit request =>

    request.body.validate(createForm) fold(
      errors => {
        Future(BadRequest(Json.toJson(errors)))
      },
      title => {
        db.createBox(title, Some(UUID.randomUUID())).map(box => Created(Json.obj("box" -> Json.toJson(box))))
      }
    )
  }

  def listBoxes = Action.async { implicit request =>
   db.listBoxes.map(boxes => Ok(Json.obj("boxes" -> Json.toJson(boxes))))
  }


}
