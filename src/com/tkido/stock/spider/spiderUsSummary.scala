package com.tkido.stock.spider

object SpiderUsSummary {
  import com.tkido.tools.Html
  import com.tkido.tools.Log
  import com.tkido.tools.tryOrElse
  
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderUsSummary Spidering ${code}"
    
    def get :Map[String, String] = {
      val html = Html("http://finance.yahoo.com/q?s=%s".format(code))
  
      val name =
        html.getGroupOf("""^.*?<div class="title"><h2>(.*?) \(.*?\)</h2>.*$""".r).replaceAll("&amp;", "&")
      val market =
        html.getGroupOf("""^.*?<span class="rtq_exch"><span class="rtq_dash">-</span>(.*?)  </span>.*$""".r)
      val price =
        html.getGroupOf("""^.*?<span class="time_rtq_ticker"><span id="yfs_l84_.*?">(.*?)</span></span>.*$""".r)
      val ratio = {
        val raw = html.getGroupOf("""^.*?<div class="title">.*?class=".*?_arrow" alt=".*?">   .*?</span><span id="yfs_p43_.*?">\((.*?)%\)</span>.*$""".r)
        val sign = html.getGroupOf("""^.*?<div class="title">.*?class=".*?_arrow" alt="(.*?)".*$""".r) match{
          case "Up"   => ""
          case "Down" => "-"
          case _      => ""
        }
        sign + raw
      }
      val volume = {
        val raw = html.getGroupOf("""^.*?Volume:</th><td class="yfnc_tabledata1"><span id="yfs_v53_.*?">(.*?)</span>.*$""".r)
        "=" + raw.replaceAll(",", "") + "/【発行】"
      }
      val lastClose =
        html.getGroupOf("""^.*?Prev Close:</th><td class="yfnc_tabledata1">(.*?)</td>.*$""".r)
      val bid =
        html.getGroupOf("""^.*?Bid:</th><td class="yfnc_tabledata1"><span id="yfs_b00_.*?">(.*?)</span>.*$""".r)
      val bidNum =
        html.getGroupOf("""^.*?Bid:</th><td class="yfnc_tabledata1"><span id="yfs_b00_.*?">.*?</span><small> x <span id="yfs_b60_.*?">(.*?)</span>.*$""".r)
      val ask =
        html.getGroupOf("""^.*?Ask:</th><td class="yfnc_tabledata1"><span id="yfs_a00_.*?">(.*?)</span>.*$""".r)
      val askNum =
        html.getGroupOf("""^.*?Ask:</th><td class="yfnc_tabledata1"><span id="yfs_a00_.*?">.*?</span><small> x <span id="yfs_a50_.*?">(.*?)</span>.*$""".r)
      
      Map("名称"   -> name,
          "市"     -> market,
          "現値"   -> price,
          "前比"   -> ratio,
          "出来"   -> volume,
          "前終"   -> lastClose,
          "最売"   -> ask,
          "最売数" -> askNum,
          "最買"   -> bid,
          "最買数" -> bidNum )
    }
    
    tryOrElse(get _, Map())
  }
}