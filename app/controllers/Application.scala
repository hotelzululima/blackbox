package controllers

import javax.inject._

import org._3dr.blackbox._
import org._3dr.blackbox.dal.BoxRepository
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Application @Inject() (db: BoxRepository)
                            (implicit ec: ExecutionContext) extends Controller{

  //test adding a box
  /*
  val insertBox = db.createBox("Box Title", Some(UUID.fromString("3e536d2c-2cbb-4475-a07e-ce7776893f03")))
  insertBox onComplete{
    case Success(boxInstance) => println(boxInstance)
    case Failure(t) => println("An error occured" + t.getMessage)
  }
  */
  def index = Action {

    Ok(views.html.index("Your new application is ready."))

  }

}
