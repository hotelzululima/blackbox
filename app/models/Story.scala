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

