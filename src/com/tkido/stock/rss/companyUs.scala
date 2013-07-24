package com.tkido.stock.rss

class CompanyUs(code:String) extends Company(code) {
  println("CompanyUs:%s".format(code))
  val data = makeData
  println(data)
  
  def makeData :Map[String, String] = {
    parseMainPage
  }
  
  def parseMainPage :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q?s=%s".format(code))
    val line = html.getLineOf("""^.*?<div class="title"><h2>(.*?) \(.*?\)</h2>.*$""".r)
    val re = """^.*?<div class="title"><h2>(.*?) \(.*?\)</h2> <span class="rtq_exch"><span class="rtq_dash">-</span>(.*?)  </span></div></div><div class="yfi_rt_quote_summary_rt_top"><p> <span class="time_rtq_ticker"><span id="yfs_l84_.*?">(.*?)</span></span> <span class=".*? time_rtq_content"><span id="yfs_c63_.*?"><img width="10" height="14" style="margin-right:-2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class=".*?_arrow" alt="(.*?)">   .*?</span><span id="yfs_p43_.*?">\((.*?)\)</span>.*$""".r
    val m = re.findFirstMatchIn(line).get
    
    val sign = if(m.group(4) == "Down") "-" else ""
    val ratio = sign + m.group(5)
    
    def getVolume = {
      val raw = html.getGroupOf("""^.*?Volume:</th><td class="yfnc_tabledata1"><span id="yfs_v53_.*?">(.*?)</span>.*$""".r)
      raw.replaceAll(",", "") + "/�y���s�z"
    }
    def getLastClose =
      html.getGroupOf("""^.*?Prev Close:</th><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
    def get52wkHigh = {
      val raw = html.getGroupOf("""^.*?52wk Range:</th><td class="yfnc_tabledata1"><span>.*?</span> - <span>(.*?)</span></td>.*$""".r)
      raw + "/�y�l�z"
    }
    def get52wkLow = {
      val raw = html.getGroupOf("""^.*?52wk Range:</th><td class="yfnc_tabledata1"><span>(.*?)</span> - <span>.*?</span></td>.*$""".r)
      raw + "/�y�l�z"
    }
    Map("����" -> m.group(1),
        "�s"   -> m.group(2),
        "���l" -> m.group(3),
        "�O��" -> ratio,
        "�N��" -> get52wkHigh,
        "�N��" -> get52wkLow,
        "�o��" -> getVolume,
        "�O�I" -> getLastClose )
  }
  
  
}
object CompanyUs{
  def apply(code:String) = {
    new CompanyUs(code)
  }
}