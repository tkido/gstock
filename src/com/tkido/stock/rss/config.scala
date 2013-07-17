package com.tkido.stock.rss

object Config {
  val prop = new java.util.Properties()
  prop.load(new java.io.FileInputStream("data/rss/conf.properties"))
  
  def get(key:String) :String =
    prop.getProperty(key)
  
  def offset :Int =
    prop.getProperty("offset").toInt
  
  
}