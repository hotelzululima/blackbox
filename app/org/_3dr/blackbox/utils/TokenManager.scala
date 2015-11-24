package org._3dr.blackbox.utils

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJsonString, JwtHeader}
import play.api.libs.json.{Writes, Json}
import org._3dr.blackbox.utils.Config

/**
  * Created by genarorg on 11/24/15.
  */
trait TokenManager {

  def createJWT[T](contextObj: T)(implicit tjs: Writes[T]): String = {
    val header = JwtHeader("HS256")
    val claimSetObj = Json.obj("iss"->"BB", "exp" -> Config.SessionExpiration, "context" -> Json.toJson(contextObj))
    val claimsSet = JwtClaimsSetJsonString(claimSetObj.toString())
    JsonWebToken(header, claimsSet, Config.AppSecret).toString
  }
}

object TokenManager extends TokenManager