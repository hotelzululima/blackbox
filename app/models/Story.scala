package models

import java.sql.Timestamp
import java.util.UUID

import slick.driver.JdbcProfile

/**
  * Created by rroche on 11/4/15.
  */

/**
  * Models a Story.
  * @param id The unique id of the object.
  * @param title Title for the story.
  * @param created date when the Story was created.
  * @param dronekitMedia Unique id for the DroneKit-Cloud Media object this Story belongs to.
  */
case class Story (id: UUID = java.util.UUID.randomUUID(),
                  title: String,
                  created: Option[Timestamp],
                  dronekitMedia: Option[UUID])

trait StoryTable {
  protected val driver: JdbcProfile
  import driver.api._

  class Stories(tag: Tag) extends Table[Story](tag, "STORY") {
    def id = column[UUID]("id", O.PrimaryKey)
    def title = column[String]("title")
    def created = column[Timestamp]("created")
    def dronekitMedia = column[UUID]("dronekitMedia")

    def * = (id, title, created.?, dronekitMedia.?) <> (Story.tupled, Story.unapply)
  }
}