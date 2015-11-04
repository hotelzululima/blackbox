package controllers

import models.{StoryTable, BoxTable}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.mvc._
import slick.driver.JdbcProfile

object Application extends Controller with BoxTable with StoryTable with HasDatabaseConfig[JdbcProfile] {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._

//  val Bxes = TableQuery[Boxes]
//  val Strs = TableQuery[Stories]

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}