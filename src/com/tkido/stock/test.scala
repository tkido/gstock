package com.tkido.stock

import com.tkido.stock.spider.Ranking
import com.tkido.stock.spider.Spider
import com.tkido.tools.Log
import com.tkido.tools.Tengine

object test extends App {
  Log open Config.logLevel
  
  Log d com.tkido.stock.tdnet.XbrlParser("C:\\OLS\\xbrl\\tdnet\\4834\\tse-qcedjpsm-48340-20150924402095-ixbrl.htm")
  
  //val te = Tengine("data/rss/test.html")
  //val text = te(Map("title" -> "テストだよ！"))
  //Log d text
  
  //Log d Ranking("2121")
  //Log d Spider("3085")
  
  Log close
}