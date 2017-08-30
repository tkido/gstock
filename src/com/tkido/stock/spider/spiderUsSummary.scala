package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.Search

object SpiderUsSummary {
  var rule = List(
      Search("名称", """^.*?<div class="title"><h2>(.*?) \(.*?\)</h2>.*$""".r, Search.GROUP, _.replaceAll("&amp;", "&")),
      Search("市", """^.*?<span class="rtq_exch"><span class="rtq_dash">-</span>(.*?)  </span>.*$""".r, Search.GROUP, s => s),
      Search("現値", """^.*?<span class="time_rtq_ticker"><span id="yfs_l84_.*?">(.*?)</span></span>.*$""".r, Search.GROUP, s => s),
      Search("出来", """^.*?Volume:</th><td class="yfnc_tabledata1"><span id="yfs_v53_.*?">(.*?)</span>.*$""".r, Search.GROUP, raw => "=%s/【発行】".format(raw.replaceAll(",", ""))),
      Search("前終", """^.*?Prev Close:</th><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, s => s),
      Search("最売", """^.*?Ask:</th><td class="yfnc_tabledata1"><span id="yfs_a00_.*?">(.*?)</span>.*$""".r, Search.GROUP, s => s),
      Search("最買", """^.*?Bid:</th><td class="yfnc_tabledata1"><span id="yfs_b00_.*?">(.*?)</span>.*$""".r, Search.GROUP, s => s),
      Search("最売数", """^.*?Ask:</th><td class="yfnc_tabledata1"><span id="yfs_a00_.*?">.*?</span><small> x <span id="yfs_a50_.*?">(.*?)</span>.*$""".r, Search.GROUP, s => s),
      Search("最買数", """^.*?Bid:</th><td class="yfnc_tabledata1"><span id="yfs_b00_.*?">.*?</span><small> x <span id="yfs_b60_.*?">(.*?)</span>.*$""".r, Search.GROUP, s => s),
      Search("raw", """^.*?<div class="title">.*?class=".*?_arrow" alt=".*?">   .*?</span><span id="yfs_p43_.*?">\((.*?)%\)</span>.*$""".r, Search.GROUP, s => s),
      Search("sign", """^.*?<div class="title">.*?class=".*?_arrow" alt="(.*?)".*$""".r, Search.GROUP, s => s match{
        case "Up"   => ""
        case "Down" => "-"
        case _      => ""
      })
    )
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderUsSummary Spidering ${code}"
    
    val html = Html(s"https://finance.yahoo.com/q?s=${code}")
    val map = html.search(rule)
    map ++ Map("前比" -> (map.getOrElse("sign", "") + map.getOrElse("raw", "")))
  }
}