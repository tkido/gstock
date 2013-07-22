package com.tkido.stock.xbrl

object Config {
  val prop = new java.util.Properties()
  prop.load(new java.io.FileInputStream("data/xbrl/conf.properties"))

  val rootPath = get("rootPath")
  
  def get(key:String) :String =
    prop.getProperty(key)
  
  
}