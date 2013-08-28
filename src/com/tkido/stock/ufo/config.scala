package com.tkido.stock.ufo

object Config {
  val prop = new java.util.Properties()
  prop.load(new java.io.FileInputStream("data/ufo/conf.properties"))

  val loglevel = get("loglevel").toInt
  val rootPath = get("rootPath")
  
  def get(key:String) :String =
    prop.getProperty(key)
}