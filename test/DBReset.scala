package test

import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions

import org.scalatest._
import play.api.test.FakeApplication
import org.scalatestplus.play._


/**
  * Created by genarorg on 11/17/15.
  */
class DBReset extends PlaySpec with OneAppPerSuite with BeforeAndAfterAll { this: Suite =>

  override def beforeAll() = {
    println("Applying db evolutions")
    val dbapi = app.injector.instanceOf[DBApi]
    Evolutions.applyEvolutions(dbapi.database("default"))
  }

  override def afterAll() = {
    println("Rolling back db evolutions")
    val fakeApp: FakeApplication = new FakeApplication()
    val dbapi = fakeApp.injector.instanceOf[DBApi]
    Evolutions.cleanupEvolutions(dbapi.database("default"))
  }

}
