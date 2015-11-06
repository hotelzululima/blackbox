package models

import java.sql.Timestamp
import java.util.UUID
import slick.driver.JdbcProfile

/**
  * Created by rroche on 11/3/15.
  */

/**
  * Models a Box.
  *
  * A Box is the main container for a support ticket and all its logs (Stories).
  *
  * @param id The unique id of the object.
  * @param title Title for the box.
  * @param created date when the box was created.
  * @param dronekitMission Unique id for the DroneKit-Cloud Mission object this Box belongs to.
  */
case class Box(id: UUID = java.util.UUID.randomUUID(),
               title: String,
               created: Option[Timestamp],
               dronekitMission: Option[UUID])

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