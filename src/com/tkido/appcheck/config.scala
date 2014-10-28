package com.tkido.appcheck

object Config {
  import com.tkido.tools.Properties
  
  private val prop = Properties("data/appcheck/conf.properties")
  
  val logLevel = prop("logLevel").toInt
  val limit = prop("limit").toInt
  val dataPath = prop("dataPath")
  
  
}