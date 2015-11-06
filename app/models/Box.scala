package models

import java.util.UUID
import slick.driver.JdbcProfile

/**
  * Created by rroche on 11/3/15.
  */
case class Box(title: String,
               dronekitMission: Option[UUID],
               id: UUID = java.util.UUID.randomUUID())

trait BoxTable {
  protected val driver: JdbcProfile
  import driver.api._

  class Boxes(tag: Tag) extends Table[Box](tag, "Boxes") {
    def id = column[UUID]("id", O.PrimaryKey)
    def title = column[String]("title")
    def dronekitMission = column[UUID]("dronekitMission")

    def * = (title, dronekitMission.?, id) <> (Box.tupled, Box.unapply)
  }
}