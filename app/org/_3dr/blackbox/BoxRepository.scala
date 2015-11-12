package org._3dr.blackbox

import java.sql.Timestamp
import java.util.{Date, UUID}
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

import models.{Vehicle, Box, Story}

/**
  * A repository for Boxes and their Stories.
  *
  * @param dbConfigProvider The Play db config provider.
  */

@Singleton
class BoxRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  // We want the JdbcProfile for this provider
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import driver.api._


  /**
    * Boxes Table
    */

  class BoxesTable(tag: Tag) extends Table[Box](tag, "Boxes") {

    //table column definitions
    def id = column[UUID]("id", O.PrimaryKey)
    def title = column[String]("title")
    def created = column[Timestamp]("created")
    def dronekitMission = column[UUID]("dronekitMission")

    //mapping columns to the Box object
    def * = (id, title, created, dronekitMission.?) <> ((Box.apply _).tupled, Box.unapply)
  }

  //this is our table accessor. We will run our queries on it.
  val boxes = TableQuery[BoxesTable]

  /**
    * Stories Table
    */

  class StoriesTable(tag: Tag) extends Table[Story](tag, "Stories") {
    def id = column[UUID]("id", O.PrimaryKey)
    def boxId = column[UUID]("box") //this will be a foreign key, setup is below.
    def title = column[String]("title")
    def created = column[Timestamp]("created")
    def dronekitMedia = column[UUID]("dronekitMedia")

    def * = (id, title, created.?, dronekitMedia.?) <> (Story.tupled, Story.unapply)

    //setup the foreign key. Delete stories when their box is deleted.
    def box = foreignKey("box_fk", boxId, boxes)(_.id, onDelete = ForeignKeyAction.Cascade)
  }

  val stories = TableQuery[StoriesTable]

  /**
    * Vehicles Table
    */

  class VehiclesTable(tag: Tag) extends Table[Vehicle](tag, "Vehicles") {
    def id = column[UUID]("id", O.PrimaryKey)
    def mac = column[String]("mac")
    def created = column[Timestamp]("created")
    def dronekitVehicle = column[UUID]("dronekitVehicle")

    def * = (id, mac.?, created, dronekitVehicle.?) <> ((Vehicle.apply _).tupled, Vehicle.unapply)
  }

  val vehicles = TableQuery[VehiclesTable]

  /**
    * Creators and modifiers
    */

  /**
    * Creates a Box.
    *
    * This function creates Box and returns a future containing a reference to it.
    *
    * @param title Title for the box.
    * @param dronekitMission Unique id for the DroneKit-Cloud Mission object this Box belongs to.
    */
  def createBox(title: String, dronekitMission: Option[UUID]): Future[Box] = db.run {

    lazy val boxesInsert = boxes returning boxes.map(_.id) into ((box, id) => box.copy())
    boxesInsert += Box(title = title, dronekitMission = dronekitMission)
  }

  /**
    * List all Boxes.
    *
    * This function returns a list of all existing boxes
    *
    */
  def listBoxes: Future[Seq[Box]] = db.run {
    boxes.result
  }

  /**
    * Find Box by ID.
    *
    * This function returns a box by its id
    * @param boxId ID for the Box
    */
  def findBoxById(boxId: UUID): Future[Option[Box]] = db.run {
    boxes.filter(_.id === boxId).result.headOption
  }

  def createVehicle(mac: Option[String], dronekitVehicle: Option[UUID]): Future[Vehicle] = db.run {

    lazy val vehiclesInsert = vehicles returning vehicles.map(_.id) into ((vehicle, id) => vehicle.copy())
    vehiclesInsert += Vehicle(mac = mac, dronekitVehicle = dronekitVehicle)
  }

  /**
    * List all Vehicles.
    *
    * This function returns a list of all existing vehicles
    *
    */
  def listVehicles: Future[Seq[Vehicle]] = db.run {
    vehicles.result
  }

  /**
    * Find vehicle by ID.
    *
    * This function returns a vehicle by its id
    * @param vehicleId ID for the Vehicle
    */
  def findVehicleById(vehicleId: UUID): Future[Option[Vehicle]] = db.run {
    vehicles.filter(_.id === vehicleId).result.headOption
  }
}
