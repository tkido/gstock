package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Search
import com.tkido.tools.Log
import com.tkido.tools.tryOrElse

object SpiderJpConsolidate {
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderJpConsolidate Spidering ${code}"
    
    val html = Html("http://profile.yahoo.co.jp/consolidate/%s".format(code))
    val searchList = List(
      Search("決算", """<td bgcolor="#ebf4ff">決算発表日</td>""".r, 2, _.replaceFirst("---", "-")),
      Search("自", """<td bgcolor="#ebf4ff">自己資本比率</td>""".r, 2, _.replaceFirst("---", "-")),
      Search("ROE", """<td bgcolor="#ebf4ff">ROE（自己資本利益率）</td>""".r, 2, _.replaceFirst("---", "-")) )
    html.search(searchList)
  }
}