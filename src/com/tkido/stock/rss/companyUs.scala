package com.tkido.stock.rss

class CompanyUs(code:String) extends Company(code) {
  println("CompanyUs:%s".format(code))
  val data = makeData
  
  def makeData :Map[String, String] = {
    parseMainPage
  }
  
  def parseMainPage :Map[String, String] = {
    val html = Html("http://finance.yahoo.com/q?s=%s".format(code))
    println(html.getPreviousLineOf("""^.*?<div class="title"><h2>(.*?) \(.*?\)</h2> <span class="rtq_exch"><span class="rtq_dash">-</span>(.*?)  </span></div></div><div class="yfi_rt_quote_summary_rt_top"><p> <span class="time_rtq_ticker"><span id="yfs_l84_.*?">(.*?)</span></span> <span class="up_g time_rtq_content"><span id="yfs_c63_.*?"><img width="10" height="14" style="margin-right:-2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class="pos_arrow" alt="Up">   .*?</span><span id="yfs_p43_.*?">\((.*?)\)</span>.*$""".r))

    Map("”­s" -> "")
  }  
  
  
}
object CompanyUs{
  def apply(code:String) = {
    new CompanyUs(code)
  }
}