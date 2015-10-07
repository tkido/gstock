package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.tryOrElse

object SpiderUsProfile {
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderUsProfile Spidering ${code}"
    
    def get :Map[String, String] = {
      val html = Html("http://finance.yahoo.com/q/pr?s=%s+Profile".format(code))
      
      val featrue :String =
        html.getGroupOf("""^.*?<span class="yfi-module-title">Business Summary</span></th><th align="right">&nbsp;</th></tr></table><p>(.*?)</p>.*$""".r)
      val employees :String =
        html.getGroupOf("""^.*?Full Time Employees:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r).replaceAll(",", "")
      val sector :String =
        html.getGroupOf("""^.*?Sector:</td><td class="yfnc_tabledata1"><a href=".*?">(.*?)</a></td>.*$""".r)
      
      Map("分類" -> sector,
          "特色" -> featrue,
          "従連" -> employees)
    }
    
    tryOrElse(get _, Map())
  }
}