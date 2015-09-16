package com.dddr.blackbox.services

import com.dddr.blackbox.http.Protocol
import com.dddr.blackbox.http.routes.BaseServiceRoute
import io.dronekit.CouchbaseScala

/**
 * Created by Jason Martens <jason.martens@3drobotics.com> on 9/16/15.
 *
 * Base service for sharing dependencies
 */
trait BaseService extends BaseServiceRoute {
  protected implicit val couchbase: CouchbaseScala
  protected implicit val protocol: Protocol
}
