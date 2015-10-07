package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.tryOrElse

object SpiderJpConsolidate {
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderJpConsolidate Spidering ${code}"
    
    def get():Map[String, String] = {
      val html = Html("http://profile.yahoo.co.jp/consolidate/%s".format(code))
      
      val settlement =
        html.getNextLineOf("""<td bgcolor="#ebf4ff">決算発表日</td>""".r).replaceFirst("---", "-")
      val capitalToAssetRatio =
        html.getNextLineOf("""<td bgcolor="#ebf4ff">自己資本比率</td>""".r).replaceFirst("---", "-")
      val roe =
        html.getNextLineOf("""<td bgcolor="#ebf4ff">ROE（自己資本利益率）</td>""".r).replaceFirst("---", "-")
      
      Map("決算" -> settlement,
          "自"   -> capitalToAssetRatio,
          "ROE"  -> roe)
    }
    tryOrElse(get _, Map())
  }
}