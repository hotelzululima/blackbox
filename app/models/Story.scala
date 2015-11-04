package models

import java.sql.Timestamp
import java.util.UUID

import slick.driver.JdbcProfile

/**
  * Created by rroche on 11/4/15.
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