package org._3dr.blackbox.dal

import javax.inject.{Inject, Singleton}

import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * A repository for Boxes and their Stories.
  *
  * @param dbConfigProvider The Play db config provider.
  */

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {


  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import driver.api._

  /**
    * Users Table
    */

  class UsersTable(tag: Tag) extends Table[User](tag, "Users") {

    //table column definitions
    def email = column[String]("email", O.PrimaryKey)
    def roleId = column[Int]("role_id")
    def * = (email, roleId) <> ((User.apply _).tupled, User.unapply)
    def role = foreignKey("Users_role_id_fk", roleId, roles)(_.id)

  }
  /**
    * Roles Table
    */

  class RolesTable(tag: Tag) extends Table[Role](tag, "Roles") {

    //table column definitions
    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name) <>((Role.apply _).tupled, Role.unapply)

  }

  lazy val users = TableQuery[UsersTable]
  lazy val roles = TableQuery[RolesTable]

  /**
    * Find User by ID (email).
    *
    * This function returns a user by its email
    * @param userEmail ID for the User
    */
  def findUserByEmail(userEmail: String): Future[Option[User]] = db.run {
    users.filter(_.email === userEmail).result.headOption
  }

  /**
    * Creates a User.
    *
    * This function creates a User and returns a future containing a reference to it.
    *
    * @param userEmail User email
    * @param userRoleId User role identifier
    */
  def createUser(userEmail: String, userRoleId: Int): Future[User] = db.run {
    lazy val userInsert = users returning users.map(_.email) into ((user, email) => user.copy())
    userInsert += User(email = userEmail, roleId = userRoleId)
  }

  /**
    * List all Users.
    *
    * This function returns a list of all existing boxes
    *
    */
  def listUsers: Future[Seq[User]] = db.run {
    users.result
  }



}
