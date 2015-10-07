package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.tryOrElse

object SpiderUsKeyStatistics {
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderUsKeyStatistics Spidering ${code}"
    
    def get :Map[String, String] = {
      val html = Html("http://finance.yahoo.com/q/ks?s=%s+Key+Statistics".format(code))
      
      val yearHigh = {
        val raw = html.getGroupOf("""^.*?52-Week High \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
        "=%s/【値】".format(raw)
      }
      val yearHighDate =
        html.getGroupOf("""^.*?52-Week High \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r)
      val yearLow = {
        val raw = html.getGroupOf("""^.*?>52-Week Low \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
        "=%s/【値】".format(raw)
      }
      val yearLowDate =
        html.getGroupOf("""^.*?>52-Week Low \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r)
      val outstanding :String = {
        val raw = html.getGroupOf("""^.*?>Shares Outstanding<font size="-1"><sup>5</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
        unRound(raw).dropRight(3)
      }
      val divYield = {
        val raw = html.getGroupOf("""^.*?Trailing Annual Dividend Yield.*?Trailing Annual Dividend Yield<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r) 
        if(raw == "N/A") "0.0%" else raw
      }
      val per = {
        val raw = html.getGroupOf("""^.*?Trailing P/E \(ttm, intraday\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
        if(raw == "N/A") "0.0" else raw
      }
      val roe = {
        val raw = html.getGroupOf("""^.*?Return on Equity \(ttm\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
        if(raw == "N/A") "-" else raw
      }
      val pbr =
        html.getGroupOf("""^.*?Price/Book \(mrq\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      
      Map("発行"   -> outstanding,
          "利"     -> divYield,
          "PER"    -> per,
          "ROE"    -> roe,
          "PBR"    -> pbr,
          "年高"   -> yearHigh,
          "年高日" -> yearHighDate,
          "年安"   -> yearLow,
          "年安日" -> yearLowDate )
    }
    
    def unRound(source:String) :String = {
      val mantissa = source.init.replaceFirst("""\.""", "")
      val unit = source.last match{
        case 'B' => "0000000"
        case 'M' => "0000"
        case 'K' => "0"
      }
      mantissa + unit
    }
    
    tryOrElse(get _, Map())
  }
}