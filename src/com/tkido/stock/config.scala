package com.tkido.stock

import com.tkido.tools.Properties

object Config {
  private val prop = Properties("data/conf.properties")
  
  val logLevel = prop("logLevel").toInt
  val xbrlPath = prop("xbrlPath")
}