package controllers

import models._
import org._3dr.blackbox._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.mvc._
import slick.driver.JdbcProfile
import java.util.UUID
import scala.util.{Success, Failure}

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject._

class Application @Inject() (db: BoxRepository)
                            (implicit ec: ExecutionContext) extends Controller{

  //test adding a box

  val insertBox = db.createBox("Box Title", Some(UUID.fromString("3e536d2c-2cbb-4475-a07e-ce7776893f03")))
  insertBox onComplete{
    case Success(boxInstance) => println(boxInstance)
    case Failure(t) => println("An error occured" + t.getMessage)

  }

  def index = Action {

    Ok(views.html.index("Your new application is ready."))

  }

}
