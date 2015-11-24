package org._3dr.blackbox.utils

/**
  * Created by genarorg on 11/23/15.
  */
import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load()
  private val zendeskConfig = config.getConfig("zendesk")

  val ZenApiHost = zendeskConfig.getString("apiHost")
  val ZenApiNameSpace = zendeskConfig.getString("apiNameSpace")

  val AppSecret = config.getString("application.secret")
  val SessionExpiration = config.getString("play.http.session.maxAge")
  val JWTHeaderName = config.getString("play.http.session.jwtName")
  val JWTPrefix = config.getString("play.http.session.tokenPrefix")

}
object Config extends Config