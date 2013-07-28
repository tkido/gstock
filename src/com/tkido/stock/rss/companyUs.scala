package com.tkido.stock.rss

class CompanyUs(code:String) extends Company(code) {
  println("CompanyUs:%s".format(code))
  val data = makeData
  println(data)
  
  def makeData :Map[String, String] = {
    makeOtherData ++ parseKeyStatistics ++ parseSummary //++ parseProfile 
  }
  
  def parseSummary :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q?s=%s".format(code))

    def getName =
      html.getGroupOf("""^.*?<div class="title"><h2>(.*?) \(.*?\)</h2>.*$""".r)
    def getMarket =
      html.getGroupOf("""^.*?<span class="rtq_exch"><span class="rtq_dash">-</span>(.*?)  </span>.*$""".r)
    def getPrice =
      html.getGroupOf("""^.*?<span class="time_rtq_ticker"><span id="yfs_l84_.*?">(.*?)</span></span>.*$""".r)
    def getRatio = {
      val raw = html.getGroupOf("""^.*?class=".*?_arrow" alt=".*?">   (.*?)</span>.*$""".r)
      println(html.getGroupOf("""^.*?class=".*?_arrow" alt="(.*?)".*$""".r))
      val sign = html.getGroupOf("""^.*?class=".*?_arrow" alt="(.*?)".*$""".r) match{
        case "Up"   => ""
        case "Down" => "-"
      }
      sign + raw + "%"
    }
    def getVolume = {
      val raw = html.getGroupOf("""^.*?Volume:</th><td class="yfnc_tabledata1"><span id="yfs_v53_.*?">(.*?)</span>.*$""".r)
      raw.replaceAll(",", "") + "/【発行】"
    }
    def getLastClose =
      html.getGroupOf("""^.*?Prev Close:</th><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
    def getPer =
      html.getGroupOf("""^.*?P/E <span class="small">(ttm)</span>:</th><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
    def getDivYield = {
      val raw = html.getGroupOf("""^.*?Div &amp; Yield:</th><td class="yfnc_tabledata1">.*? \((.*?)\) </td>.*$""".r) 
      if(raw == "N/A") "-" else raw
    }
    
    Map("名称" -> getName,
        "市"   -> getMarket,
        "現値" -> getPrice,
        "前比" -> getRatio,
        "出来" -> getVolume,
        "利"   -> getDivYield,
        "PER"  -> getPer,
        "前終" -> getLastClose )
  }
  
  def parseProfile :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q/pr?s=%s+Profile".format(code))
    def getFeatrue :String =
      html.getGroupOf("""^.*?<span class="yfi-module-title">Business Summary</span></th><th align="right">&nbsp;</th></tr></table><p>(.*?)</p>.*$""".r)
    Map("特色" -> getFeatrue)
  }
  
  def parseKeyStatistics :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q/ks?s=%s+Key+Statistics".format(code))
    def get52wkHigh = {
      val raw = html.getGroupOf("""^.*?52-Week High \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      raw + "/【値】"
    }
    def get52wkHighDate =
      html.getGroupOf("""^.*?52-Week High \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r)
    def get52wkLow = {
      val raw = html.getGroupOf("""^.*?>52-Week Low \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      raw + "/【値】"
    }
    def get52wkLowDate =
      html.getGroupOf("""^.*?>52-Week Low \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r)
    def getOutstanding :String = {
      val raw = html.getGroupOf("""^.*?>Shares Outstanding<font size="-1"><sup>5</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      roundedNumberStringToString(raw)
    }
      
    Map("発行"   -> getOutstanding,
        "年高"   -> get52wkHigh,
        "年高日" -> get52wkHighDate,
        "年安"   -> get52wkLow,
        "年安日" -> get52wkLowDate )
  }
  
  def roundedNumberStringToString(source:String) :String = {
    val mantissa = source.init.replaceFirst("""\.""", "")
    val unit = source.last match{
      case 'B' => "0000000"
      case 'M' => "0000"
      case 'T' => "0"
    }
    mantissa + unit
  }
  
}
object CompanyUs{
  def apply(code:String) = {
    new CompanyUs(code)
  }
}