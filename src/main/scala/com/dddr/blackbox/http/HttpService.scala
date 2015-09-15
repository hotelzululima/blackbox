package com.dddr.blackbox.http

import akka.http.scaladsl.server.Directives._
import com.dddr.blackbox.http.routes.{ViewServiceRoute, BoxServiceRoute}
import com.dddr.blackbox.utils.ExtendedSupport

/**
 * Created by rroche on 9/8/15.
 */
trait HttpService extends BoxServiceRoute with ViewServiceRoute with ExtendedSupport {
  val routes = extendedSupportHandler {
        viewRoutes ~
          pathPrefix("api" / "v1") {
            boxRoutes
          }
    }
}
