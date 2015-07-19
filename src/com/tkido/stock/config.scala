package com.tkido.stock

object Config {
  import com.tkido.tools.Properties
  
  private val prop = Properties("data/conf.properties")
  //common
  val logLevel = prop("logLevel").toInt
  val xbrlPath = prop("xbrlPath")
  //rss
  val offset   = prop("offset").toInt
  val rssFlag  = prop("rssFlag").toBoolean
  //patrol
  val buildNumber = prop("buildNumber")
}