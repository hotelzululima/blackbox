package com.dddr.blackbox.utils

import com.typesafe.config.ConfigFactory

/**
 * Created by rroche on 9/8/15.
 */
trait Config {
  private val config = ConfigFactory.load()

  private val assetsConfig = config.getConfig("assets")
  val assetPath = assetsConfig.getString("path")
  val viewsPath = assetsConfig.getString("views")

  private val siteConfig = config.getConfig("site")
  val viewTitle = siteConfig.getString("title")

  val couchbaseConfig = config.getConfig("couchbase")

}
