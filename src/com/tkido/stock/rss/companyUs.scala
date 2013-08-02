package com.tkido.stock.rss

class CompanyUs(code:String, row:Int) extends Company(code:String, row:Int) {
  val data = makeData
  
  def makeData :Map[String, String] = {
    val parsedData = parseKeyStatistics ++ parseSummary ++ parseProfile
    parsedData ++ makeOtherData ++ makeSpaceData
  }
  
  def makeSpaceData :Map[String, String] = {
    Map("é©"   -> "-",
        "åàéZ" -> "-",
        "óéì˙" -> "-",
        "åàä˙" -> "-",
        "ë„ï\" -> "-",
        "ê›óß" -> "-",
        "è„èÍ" -> "-",
        "îÉéc" -> "-",
        "îÉécèTç∑" -> "-",
        "îÑéc" -> "-",
        "îÑécèTç∑" -> "-",
        "è]íP" -> "-",
        "óÓ" -> "-",
        "é˚" -> "-",
        "óDë“" -> "-" )
  }
  
  def parseSummary :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q?s=%s".format(code))

    def getName =
      html.getGroupOf("""^.*?<div class="title"><h2>(.*?) \(.*?\)</h2>.*$""".r).replaceAll("&amp;", "&")
    def getMarket =
      html.getGroupOf("""^.*?<span class="rtq_exch"><span class="rtq_dash">-</span>(.*?)  </span>.*$""".r)
    def getPrice =
      html.getGroupOf("""^.*?<span class="time_rtq_ticker"><span id="yfs_l84_.*?">(.*?)</span></span>.*$""".r)
    def getRatio = {
      val raw = html.getGroupOf("""^.*?<div class="title">.*?class=".*?_arrow" alt=".*?">   .*?</span><span id="yfs_p43_.*?">\((.*?)%\)</span>.*$""".r)
      val sign = html.getGroupOf("""^.*?<div class="title">.*?class=".*?_arrow" alt="(.*?)".*$""".r) match{
        case "Up"   => ""
        case "Down" => "-"
        case _      => ""
      }
      sign + raw
    }
    def getVolume = {
      val raw = html.getGroupOf("""^.*?Volume:</th><td class="yfnc_tabledata1"><span id="yfs_v53_.*?">(.*?)</span>.*$""".r)
      "=" + raw.replaceAll(",", "") + "/Åyî≠çsÅz"
    }
    def getLastClose =
      html.getGroupOf("""^.*?Prev Close:</th><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
    def getBid =
      html.getGroupOf("""^.*?Bid:</th><td class="yfnc_tabledata1"><span id="yfs_b00_.*?">(.*?)</span>.*$""".r)
    def getBidNum =
      html.getGroupOf("""^.*?Bid:</th><td class="yfnc_tabledata1"><span id="yfs_b00_.*?">.*?</span><small> x <span id="yfs_b60_.*?">(.*?)</span>.*$""".r)
    def getAsk =
      html.getGroupOf("""^.*?Ask:</th><td class="yfnc_tabledata1"><span id="yfs_a00_.*?">(.*?)</span>.*$""".r)
    def getAskNum =
      html.getGroupOf("""^.*?Ask:</th><td class="yfnc_tabledata1"><span id="yfs_a00_.*?">.*?</span><small> x <span id="yfs_a50_.*?">(.*?)</span>.*$""".r)
    Map("ñºèÃ"   -> getName,
        "és"     -> getMarket,
        "åªíl"   -> getPrice,
        "ëOî‰"   -> getRatio,
        "èoóà"   -> getVolume,
        "ëOèI"   -> getLastClose,
        "ç≈îÑ"   -> getAsk,
        "ç≈îÑêî" -> getAskNum,
        "ç≈îÉ"   -> getBid,
        "ç≈îÉêî" -> getBidNum )
  }
  
  def parseProfile :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q/pr?s=%s+Profile".format(code))
    def getFeatrue :String =
      html.getGroupOf("""^.*?<span class="yfi-module-title">Business Summary</span></th><th align="right">&nbsp;</th></tr></table><p>(.*?)</p>.*$""".r)
    def getEmployees :String =
      html.getGroupOf("""^.*?Full Time Employees:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r).replaceAll(",", "")
    def getSector :String =
      html.getGroupOf("""^.*?Sector:</td><td class="yfnc_tabledata1"><a href=".*?">(.*?)</a></td>.*$""".r)
    Map("ï™óﬁ" -> getSector,
        "ì¡êF" -> getFeatrue,
        "è]òA" -> getEmployees)
  }
  
  def parseKeyStatistics :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q/ks?s=%s+Key+Statistics".format(code))
    def get52wkHigh = {
      val raw = html.getGroupOf("""^.*?52-Week High \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      "=%s/ÅyílÅz".format(raw)
    }
    def get52wkHighDate =
      html.getGroupOf("""^.*?52-Week High \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r)
    def get52wkLow = {
      val raw = html.getGroupOf("""^.*?>52-Week Low \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      "=%s/ÅyílÅz".format(raw)
    }
    def get52wkLowDate =
      html.getGroupOf("""^.*?>52-Week Low \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r)
    def getOutstanding :String = {
      val raw = html.getGroupOf("""^.*?>Shares Outstanding<font size="-1"><sup>5</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      unRound(raw).dropRight(3)
    }
    def getDivYield = {
      val raw = html.getGroupOf("""^.*?Trailing Annual Dividend Yield.*?Trailing Annual Dividend Yield<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r) 
      if(raw == "N/A") "0.0%" else raw
    }
    def getPer = {
      val raw = html.getGroupOf("""^.*?Trailing P/E \(ttm, intraday\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      if(raw == "N/A") "0.0" else raw
    }
    def getRoe = {
      val raw = html.getGroupOf("""^.*?Return on Equity \(ttm\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      if(raw == "N/A") "-" else raw
    }
    def getPbr =
      html.getGroupOf("""^.*?Price/Book \(mrq\):</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
    Map("î≠çs"   -> getOutstanding,
        "óò"     -> getDivYield,
        "PER"    -> getPer,
        "ROE"    -> getRoe,
        "PBR"    -> getPbr,
        "îNçÇ"   -> get52wkHigh,
        "îNçÇì˙" -> get52wkHighDate,
        "îNà¿"   -> get52wkLow,
        "îNà¿ì˙" -> get52wkLowDate )
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
  
}
object CompanyUs{
  def apply(code:String, row:Int) = {
    new CompanyUs(code, row)
  }
}