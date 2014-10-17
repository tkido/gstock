package com.tkido.stock

object test extends App {
  import com.tkido.stock.spider.Ranking
  import com.tkido.stock.spider.Spider
  import com.tkido.tools.Logger
  
  Logger.level = Config.logLevel
  
  Logger.debug(Ranking("2121"))
  Logger.debug(Spider("3085"))
  
  Logger.close()
}