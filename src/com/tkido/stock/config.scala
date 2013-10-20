package com.tkido.stock

object Config {
  import com.tkido.tools.Properties
  
  private val prop = Properties("data/conf.properties")
  
  val offset   = prop("offset").toInt
  val logLevel = prop("logLevel").toInt
  val rssFlag  = prop("rssFlag").toBoolean
  val xbrlPath = prop("xbrlPath")
}