package com.dddr.blackbox.http.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.dddr.blackbox.models.{BoxId, BoxEntityNew}
import com.dddr.blackbox.services.BoxService
import com.dddr.blackbox.utils.Config

/**
 * Created by rroche on 9/14/15.
 */
trait BoxServiceRoute extends BoxService with BaseServiceRoute with Config {
  import protocol._

  val boxRoutes =
    pathPrefix("box") {
      path(JavaUUID) { uuid =>
        get {
          complete(getBoxById(BoxId(uuid.toString())))
        }
      }
    } ~
      post {
        entity(as[BoxEntityNew]) { box =>
          onSuccess(createBox(box)) {
            case Some(newBox) => complete(Created, newBox)
            case None => complete(FailedDependency,  s"Could not create box")
          }
        }
      } ~
      get {
        complete(getListOfBoxes())
      }
}
