package test

import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions

import org.scalatest._
import play.api.test.FakeApplication


/**
  * Created by genarorg on 11/17/15.
  */
trait DBReset extends BeforeAndAfter { this: Suite =>

  implicit val app: FakeApplication

  before {
    val dbapi = app.injector.instanceOf[DBApi]
    Evolutions.applyEvolutions(dbapi.database("default"))
  }

  after {
    val dbapi = app.injector.instanceOf[DBApi]
    Evolutions.cleanupEvolutions(dbapi.database("default"))
  }

}
