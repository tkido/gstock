package com.tkido.stock

import com.tkido.tools.Properties

object Config {
  private val prop = Properties("data/conf.properties")
  //common
  lazy val logLevel = prop("logLevel").toInt
  lazy val xbrlPath = prop("xbrlPath")
  
  //for rss
  lazy val offset   = prop("offset").toInt
  lazy val rssFlag  = prop("rssFlag").toBoolean
  
  //for patrol
  
  //for appcheck
  lazy val limit = prop("limit").toInt
  lazy val dataPath = prop("dataPath")
}