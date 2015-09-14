package com.dddr.blackbox

import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.dddr.blackbox.http.HttpService
import com.dddr.blackbox.utils.CouchbaseMigration

import scala.concurrent.ExecutionContext

/**
 * Created by rroche on 9/8/15.
 */
object Main extends App with CouchbaseMigration with HttpService {
  override protected implicit val system = ActorSystem()
  override protected implicit val executor: ExecutionContext = system.dispatcher
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  override protected val log: LoggingAdapter = Logging(system, getClass)

  migrate()

  Http().bindAndHandle(routes, "0.0.0.0", 8444)

}
