package controllers

import models.{StoryTable, BoxTable, Box, Story}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.mvc._
import slick.driver.JdbcProfile
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

import scala.concurrent.Future

object Application extends Controller with BoxTable with StoryTable with HasDatabaseConfig[JdbcProfile] {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

  val boxes = TableQuery[Boxes]

  //lazy val boxesInsert = boxes returning boxes.map(_.id)
  //val result = db.run(boxesInsert += Box(title= "test box", dronekitMission= Some(dkMission)))


  def index = Action.async { implicit request =>


    val dkMission: UUID = UUID.fromString("3e536d2c-2cbb-4475-a07e-ce7776893f03")

    lazy val boxesInsert = boxes returning boxes.map(_.id)
    val insertResult = db.run(boxesInsert += Box(title= "test box", dronekitMission= Some(dkMission)))

    insertResult onComplete {
        case Success(boxId) => println(boxId)
        case Failure(t) => println("An error has occured: " + t.getMessage)
    }

    println("HEY!!")
    println(insertResult)

    val results: Future[Seq[Box]] = db.run(boxes.result) //get all
    results.map(boxes => Ok(views.html.index(boxes.toString())))
  }

}