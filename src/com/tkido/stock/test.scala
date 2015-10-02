package com.tkido.stock

object test extends App {
  import com.tkido.stock.spider.Ranking
  import com.tkido.stock.spider.Spider
  import com.tkido.tools.Log
  import com.tkido.tools.Tengine
  
  Log.level = Config.logLevel
  
  val te = Tengine("data/rss/test.html")
  val text = te(Map("title" -> "テストだよ！"))
  Log d text
  
  Log d Ranking("2121")
  Log d Spider("3085")
  
  Log.close()
}