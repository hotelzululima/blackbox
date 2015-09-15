package com.dddr.blackbox.http.routes

import com.dddr.blackbox.models.UserEntity
import com.dddr.blackbox.utils.Config
import com.dddr.blackbox.services.{LayoutService, IndexService}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.io.Source

/**
 * Created by rroche on 9/14/15.
 */
trait ViewServiceRoute extends BaseServiceRoute with LayoutService with IndexService with Config {
  val realLayoutFile = Source.fromFile(s"$viewsPath/layout.hbs").mkString

  val viewRoutes = path(""){
      get{
        // TODO: move this to its own ServiceRouter, probably ViewsServiceRouter
        val fakeObject = UserEntity(login = "jason", password = "123", name = "JASON IS A HAXXOR")
        val fakeTemplate =
          """
            |<p>{{title}}</p><p>{{user.name}}</p>
          """.stripMargin
        complete(generateView(viewTitle, fakeObject, realLayoutFile, fakeTemplate))
      }
    }
}
