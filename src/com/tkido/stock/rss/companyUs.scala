package com.tkido.stock.rss

class CompanyUs(code:String) extends Company(code) {
  println("CompanyUs:%s".format(code))
  val data = makeData
  println(data)
  
  def makeData :Map[String, String] = {
    makeOtherData ++ parseSummary ++ parseKeyStatistics //++ parseProfile 
  }
  
  def parseSummary :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q?s=%s".format(code))
    val line = html.getLineOf("""^.*?<div class="title"><h2>(.*?) \(.*?\)</h2>.*$""".r)
    val re = """^.*?<div class="title"><h2>(.*?) \(.*?\)</h2> <span class="rtq_exch"><span class="rtq_dash">-</span>(.*?)  </span></div></div><div class="yfi_rt_quote_summary_rt_top"><p> <span class="time_rtq_ticker"><span id="yfs_l84_.*?">(.*?)</span></span> <span class=".*? time_rtq_content"><span id="yfs_c63_.*?"><img width="10" height="14" style="margin-right:-2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class=".*?_arrow" alt="(.*?)">   .*?</span><span id="yfs_p43_.*?">\((.*?)\)</span>.*$""".r
    val m = re.findFirstMatchIn(line).get
    
    val sign = if(m.group(4) == "Down") "-" else ""
    val ratio = sign + m.group(5)
    
    def getVolume = {
      val raw = html.getGroupOf("""^.*?Volume:</th><td class="yfnc_tabledata1"><span id="yfs_v53_.*?">(.*?)</span>.*$""".r)
      raw.replaceAll(",", "") + "/Åyî≠çsÅz"
    }
    def getLastClose =
      html.getGroupOf("""^.*?Prev Close:</th><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
    def getPer =
      html.getGroupOf("""^.*?P/E <span class="small">(ttm)</span>:</th><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
    def getDivYield = {
      val raw = html.getGroupOf("""^.*?Div &amp; Yield:</th><td class="yfnc_tabledata1">.*? \((.*?)\) </td>.*$""".r) 
      if(raw == "N/A") "-" else raw
    }
    
    Map("ñºèÃ" -> m.group(1),
        "és"   -> m.group(2),
        "åªíl" -> m.group(3),
        "ëOî‰" -> ratio,
        "èoóà" -> getVolume,
        "óò"   -> getDivYield,
        "PER"  -> getPer,
        "ëOèI" -> getLastClose )
  }
  
  def parseProfile :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q/pr?s=%s+Profile".format(code))
    def getFeatrue :String =
      html.getGroupOf("""^.*?<span class="yfi-module-title">Business Summary</span></th><th align="right">&nbsp;</th></tr></table><p>(.*?)</p>.*$""".r)
    Map("ì¡êF" -> getFeatrue)
  }
  
  def parseKeyStatistics :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q/ks?s=%s+Key+Statistics".format(code))
    def get52wkHigh = {
      val raw = html.getGroupOf("""^.*?52-Week High \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      raw + "/ÅyílÅz"
    }
    def get52wkHighDate =
      html.getGroupOf("""^.*?52-Week High \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r)
    def get52wkLow = {
      val raw = html.getGroupOf("""^.*?>52-Week Low \(.*?\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      raw + "/ÅyílÅz"
    }
    def get52wkLowDate =
      html.getGroupOf("""^.*?>52-Week Low \((.*?)\)<font size="-1"><sup>3</sup></font>:</td><td class="yfnc_tabledata1">.*?</td>.*$""".r)
    Map("îNçÇ"   -> get52wkHigh,
        "îNçÇì˙" -> get52wkHighDate,
        "îNà¿"   -> get52wkLow,
        "îNà¿ì˙" -> get52wkLowDate )
  }
  
  
}
object CompanyUs{
  def apply(code:String) = {
    new CompanyUs(code)
  }
}