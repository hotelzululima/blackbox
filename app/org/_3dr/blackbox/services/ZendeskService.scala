package org._3dr.blackbox.services

import com.sun.corba.se.impl.orbutil.closure.Future
import play.api.Play
import play.api.libs.ws.{WSAuthScheme, WSClient}

/**
  * Created by genarorg on 11/19/15.
  */
trait ZendeskService {

  val apiHost: String = Play.current.configuration.getString("zendesk.apiHost").get
  val apiNameSpace: String = Play.current.configuration.getString("zendesk.apiNameSpace").get

  def getUser(email: String, password: String)(implicit ws: WSClient): Future[] = {

    ws.url(apiHost+apiNameSpace+"/users/me.json").withAuth(email, password, WSAuthScheme.BASIC).get()

  }

}
