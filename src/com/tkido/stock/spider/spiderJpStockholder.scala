package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.Search
import com.tkido.tools.retry

object SpiderJpStockholder {
  val rule = List(
      Search("優待", """<tr><th>権利確定月</th><td>(.*?)</td></tr>""".r, Search.GROUP, _.replaceAll("末日", ""))
    )
  
  def apply(code:String) :Map[String, String] = {
    Log d s"spiderJpStockholder Spidering ${code}"
    retry { Html(s"https://info.finance.yahoo.co.jp/stockholder/detail/?code=${code}") } match {
      case Some(html) => html.search(rule)
      case None       => Map()
    }
  }
}