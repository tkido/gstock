package com.tkido.stock

object Config {
  import java.io.FileInputStream
  import java.util.Properties
  
  private val prop = new Properties
  prop.load(new FileInputStream("data/conf.properties"))
  
  private def get(key:String) :String = prop.getProperty(key)
  
  val offset   = get("offset").toInt
  val logLevel = get("logLevel").toInt
  val rssFlag  = get("rssFlag").toBoolean
  val xbrlPath = get("xbrlPath")
}