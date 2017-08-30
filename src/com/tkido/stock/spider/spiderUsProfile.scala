package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.Search

object SpiderUsProfile {
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderUsProfile Spidering ${code}"
    
    val html = Html(s"https://finance.yahoo.com/q/pr?s=${code}+Profile")
    html.search(List(
      Search("分類", """^.*?Sector:</td><td class="yfnc_tabledata1"><a href=".*?">(.*?)</a></td>.*$""".r, Search.GROUP, s => s),
      Search("特色", """^.*?<span class="yfi-module-title">Business Summary</span></th><th align="right">&nbsp;</th></tr></table><p>(.*?)</p>.*$""".r, Search.GROUP, s => s),
      Search("従連", """^.*?Full Time Employees:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, _.replaceAll(",", "")) )
    )
  }
}