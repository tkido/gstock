package com.tkido.stock.patrol

class Company(code:String) {
  import com.tkido.stock.edinet
  import com.tkido.stock.tdnet
  import com.tkido.tools.Html
  import com.tkido.tools.tryOrElse
  
  val data = makeData
  
  def makeData :Map[String, String] = {
    tryOrElse(makeEdinetData _, Map()) ++
    tryOrElse(parseData _, Map())
  }
  
  def makeEdinetData :Map[String, String] =
    Map("企価"   -> edinet.Company(code).fairValue.toString )
  
  def parseData :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getCurrentPrice() :String =
      html.getGroupOf("""<td class="stoksPrice">(.*?)</td>""".r).replaceFirst("---", " ")
    def getHighest() :String =
      html.getPreviousLineOf("""<dt class="title">年初来高値""".r).dropRight(10).replaceAll(",", "").replaceFirst("更新", "")
    def getPer() :String =
      html.getPreviousLineOf("""<dt class="title">PER""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "0")
    def getPbr() :String =
      html.getPreviousLineOf("""<dt class="title">PBR""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "-")
    def getOutstanding() :String =
      html.getPreviousLineOf("""<dt class="title">発行済株式数""".r).dropRight(12)
    
    Map("現値" -> getCurrentPrice,
        "年高" -> getHighest,
        "PER"  -> getPer,
        "PBR"  -> getPbr,
        "発行" -> getOutstanding )
  }
}

object Company{
  def apply(code:String) = {
    new Company(code)
  }
}
