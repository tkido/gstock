package com.tkido.stock

import com.tkido.stock.spider.SpiderJpConsolidate
import com.tkido.tools.Log

import com.tkido.tools.Html
import com.tkido.tools.Search

object test extends App {
  Log open Config.logLevel
  
  Log d SpiderJpConsolidate("3085")
  
  val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(3085))
  val searchList = List(
    Search("現値", """^.*?<td class="stoksPrice">(.*?)</td>""".r, 0, _.replaceFirst("---", "")))
  Log d html.search(searchList)
  
  Log close
}