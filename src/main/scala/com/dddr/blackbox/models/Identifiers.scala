package com.dddr.blackbox.models

import java.util.UUID

/**
 * Created by rroche on 9/8/15.
 */
trait Identifiers {
  val id: String
  val kind: String
  override def toString = id
  def key: String = s"$kind-$id"
}

case class UserId(id: String = UUID.randomUUID().toString()) extends Identifiers {
  val kind = "user"
}

case class BoxId(id: String = UUID.randomUUID().toString()) extends Identifiers {
  val kind = "box"
}