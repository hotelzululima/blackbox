package com.dddr.blackbox.http.routes

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import com.dddr.blackbox.http.Protocol

import scala.concurrent.ExecutionContext

/**
 * Created by rroche on 9/8/15.
 */
trait BaseServiceRoute extends Protocol with SprayJsonSupport {
  protected implicit def system: ActorSystem
  protected implicit def executor: ExecutionContext
  protected implicit def materializer: ActorMaterializer

  protected def log: LoggingAdapter

}
