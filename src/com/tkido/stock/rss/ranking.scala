package com.tkido.stock.rss

object Ranking {
  import com.tkido.tools.Html
  import com.tkido.tools.Logger
  
  val html = Html("http://info.finance.yahoo.co.jp/ranking/?kd=31&tm=d&vl=a&mk=1&p=1")
  val reTable = """^<tbody>(.*)</tbody>$""".r
  
  val reData = """^<tr class="rankingTabledata yjM"><td class="txtcenter">(\d{1,4})</td><td class="txtcenter"><a href="http://stocks\.finance\.yahoo\.co\.jp/stocks/detail/\?code=(\d{4}).*""".r
  
  def lineToPair(line:String): Pair[String, String] = {
    val reData(rank, code) = line
    code -> rank
  }
  val map = html.getGroupOf(reTable)
                 .split("""</tr>""")
                 .map(lineToPair)
                 .toMap
  
  Logger.debug(map)
  
  def apply(code:String): String = {
    map.getOrElse(code, "-")
  }
}