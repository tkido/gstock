package com.tkido.stock.rss

object Config {
  val prop = new java.util.Properties()
  prop.load(new java.io.FileInputStream("data/rss/conf.properties"))

  val offset = get("offset").toInt
  
  def get(key:String) :String =
    prop.getProperty(key)
  
  
}