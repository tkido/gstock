package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Search
import com.tkido.tools.Log
import com.tkido.tools.tryOrElse

object SpiderJpConsolidate {
  def clean(s:String) = s.replaceFirst("---", "-")
  val rule = List(
    Search("決算", """<td bgcolor="#ebf4ff">決算発表日</td>""".r, Search.NEXT, clean),
    Search("自", """<td bgcolor="#ebf4ff">自己資本比率</td>""".r, Search.NEXT, clean),
    Search("ROE", """<td bgcolor="#ebf4ff">ROE（自己資本利益率）</td>""".r, Search.NEXT, clean) )
  
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderJpConsolidate Spidering ${code}"
    
    val html = Html(s"https://profile.yahoo.co.jp/consolidate/${code}")
    html.search(rule)
  }
}