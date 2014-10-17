package com.tkido.stock.spider

object SpiderJpStockholder {
  import com.tkido.tools.Html
  import com.tkido.tools.Logger
  import com.tkido.tools.tryOrElse
  
  def apply(code:String) :Map[String, String] = {
    Logger.debug("spiderJpStockholder Spidering ", code)
    
    def get():Map[String, String] = {
      val html = Html("http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code))
      val month =
        html.getGroupOf("""<tr><th>権利確定月</th><td>(.*?)</td></tr>""".r).replaceAll("末日", "")
      Map("優待" -> month)
    }
    tryOrElse(get _, Map())
  }
}