package com.dddr.blackbox.http.routes

import akka.http.scaladsl.common.StrictForm
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.dddr.blackbox.models.{BoxId, BoxEntityNew}
import com.dddr.blackbox.services.{MediaService, BoxService}
import com.dddr.blackbox.utils.Config

/**
 * Created by rroche on 9/14/15.
 */
trait BoxServiceRoute extends BoxService with MediaService with BaseServiceRoute with Config {
  import protocol._

  def MediaRoute(boxId: BoxId) = pathPrefix("media") {
    pathEndOrSingleSlash {
      get {
        complete(getAllMedia(boxId))
      } ~
        post {
          formFields('file.as[StrictForm.FileData]) {
            case StrictForm.FileData(name, HttpEntity.Strict(ct, data)) ⇒ {
              onSuccess(createMediaNoStream(BoxId(boxId.toString), data, ct.mediaType.toString())) { media =>
                complete(Created, media)
              }
            }
          }
        }
    }
  }

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
