package com.dddr.blackbox

import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.dddr.blackbox.http.{Protocol, HttpService}
import com.dddr.blackbox.utils.CouchbaseMigration
import io.dronekit.CouchbaseScala

import scala.concurrent.ExecutionContext

/**
 * Created by rroche on 9/8/15.
 */
object Main extends App with HttpService with CouchbaseMigration {
  override protected implicit val system = ActorSystem()
  override protected implicit val executor: ExecutionContext = system.dispatcher
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  override val protocol = new Protocol()


  // Couchbase configuration
  val couchbase = new CouchbaseScala(
    couchbaseConfig.getString("hostname"),
    couchbaseConfig.getString("bucket"),
    couchbaseConfig.getString("password"),
    protocol)

  override val log: LoggingAdapter = Logging(system, getClass)

  migrate()

  Http().bindAndHandle(routes, "0.0.0.0", 8444)
}