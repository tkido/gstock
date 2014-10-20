package com.tkido.stock

object test extends App {
  import com.tkido.stock.spider.Ranking
  import com.tkido.stock.spider.Spider
  import com.tkido.tools.Logger
  import com.tkido.tools.Tengine
  
  Logger.level = Config.logLevel
  
  val te = Tengine("data/rss/test.html")
  val text = te.render(Map("title" -> "テストだよ！"))
  Logger.debug(text)
  
  Logger.debug(Ranking("2121"))
  Logger.debug(Spider("3085"))
  
  Logger.close()
}