package com.tkido.stock.rss

import com.tkido.tools.Properties

object Config {
  private val prop = Properties("data/rss/conf.properties")
  
  val offset   = prop("offset").toInt
  val rssFlag  = prop("rssFlag").toBoolean
}