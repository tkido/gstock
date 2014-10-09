package com.tkido.stock

object test extends App {
  import com.tkido.stock.rss.Ranking
  import com.tkido.tools.Logger
  
  Logger.level = Config.logLevel
  
  Logger.debug(Ranking("2121"))
  
  Logger.close()
}