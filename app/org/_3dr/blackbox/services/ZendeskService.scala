package org._3dr.blackbox.services

import scala.concurrent.Future
import play.api.libs.ws.{WSResponse, WSAuthScheme, WSClient}
import org._3dr.blackbox.utils._

import scala.concurrent.duration._

/**
  * Created by genarorg on 11/19/15.
  */
trait ZendeskService{

  def getUser(email: String, password: String)(implicit ws: WSClient): Future[WSResponse] = {

    ws.url(Config.ZenApiHost+Config.ZenApiNameSpace+"/users/me.json").withAuth(email, password, WSAuthScheme.BASIC).withRequestTimeout(3000 millis).get()

  }

}
