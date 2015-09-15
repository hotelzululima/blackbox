package com.dddr.blackbox.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.dddr.blackbox.http.routes.BaseServiceRoute
import com.dddr.blackbox.models._
import com.dddr.blackbox.services.{BoxService, IndexService}
import com.dddr.blackbox.utils.{ExtendedSupport, Config}

import scala.io.Source

/**
 * Created by rroche on 9/8/15.
 */
trait HttpService extends BaseServiceRoute with IndexService with BoxService with ExtendedSupport with Config {
  val fakeObject = UserEntity(login = "jason", password = "123", name = "JASON IS A HAXXOR")
  val fakeTemplate =
    """
      |<p>{{title}}</p><p>{{user.name}}</p>
    """.stripMargin

  val realLayoutFile = Source.fromFile(s"$viewsPath/layout.hbs").mkString

  val routes =
    extendedSupportHandler {
//      path(""){
//        complete(generateView(viewTitle, fakeObject, realLayoutFile, fakeTemplate))
//      } ~
        path("box") {
          path(JavaUUID) { uuid =>
            get {
              complete(getBoxById(BoxId(uuid.toString())))
            }
          }
          post {
            entity(as[BoxEntityNew]) { box =>
              complete("boxx")
              onSuccess(createBox(box)) {
                case Some(newBox) => complete(Created, newBox)
                case None => complete(FailedDependency,  s"Could not create box")
              }
            }
          }
        }
    }
}
