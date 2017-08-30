package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.Search

object SpiderUsKeyStatistics {
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderUsKeyStatistics Spidering ${code}"
    
    def unRound(source:String) :String = {
      val mantissa = source.init.replaceFirst("""\.""", "")
      val unit = source.last match{
        case 'B' => "0000000"
        case 'M' => "0000"
        case 'K' => "0"
      }
      mantissa + unit
    }
    
    val html = Html(s"https://finance.yahoo.com/q/ks?s=${code}+Key+Statistics")
    html.search(List(
      Search("発行", """^.*?>Shares Outstanding<font size="-1"><sup>5</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, unRound(_).dropRight(3)),
      Search("利", """^.*?Trailing Annual Dividend Yield.*?Trailing Annual Dividend Yield<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, raw => if(raw == "N/A") "0.0%" else raw),
      Search("PER", """^.*?Trailing P/E \(ttm, intraday\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, raw => if(raw == "N/A") "0.0" else raw),
      Search("ROE", """^.*?Return on Equity \(ttm\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, raw => if(raw == "N/A") "-" else raw),
      Search("PBR", """^.*?Price/Book \(mrq\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, s => s),
      Search("年高", """^.*?52-Week High \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, raw => s"=${raw}/【値】"),
      Search("年高日", """^.*?52-Week High \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r, Search.GROUP, s => s),
      Search("年安", """^.*?>52-Week Low \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r, Search.GROUP, raw => s"=${raw}/【値】"),
      Search("年安日", """^.*?>52-Week Low \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r, Search.GROUP, s => s) )
    )
  }
}