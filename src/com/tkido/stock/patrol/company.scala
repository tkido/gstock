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
      html.getGroupOf("""<td class="stoksPrice">(.*?)</td>""".r).replaceAll(",", "").replaceFirst("---", " ")
    def getHighest() :String =
      html.getPreviousLineOf("""<dt class="title">年初来高値""".r).dropRight(10).replaceAll(",", "").replaceFirst("更新", "")
    def getPer() :String =
      html.getPreviousLineOf("""<dt class="title">PER""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "0")
    def getPbr() :String =
      html.getPreviousLineOf("""<dt class="title">PBR""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "-")
    def getOutstanding() :String =
      html.getPreviousLineOf("""<dt class="title">発行済株式数""".r).dropRight(12).replaceAll(",", "")
    
    Map("現値" -> getCurrentPrice,
        "年高" -> getHighest,
        "PER"  -> getPer,
        "PBR"  -> getPbr,
        "発行" -> getOutstanding )
  }
  
  override def toString =
    data.toString
  
  def tdnetScore() :Int = {
    if(tdnet.Checker(code))
      50
    else
      0
  }
  
  def edinetScore() :Int = {
    val price = data("現値").toDouble
    val outstanding = data("発行").toDouble
    val fairvalue = data("企価").toDouble
    val ratio = price * outstanding * 1000 / fairvalue
    
    if(ratio < 0.3)
      0
    else if(ratio > 1.0)
      0
    else
      ((1.0 - ratio) * 100).toInt
  }
  
   def highScore() :Int = {
    val price = data("現値").toInt
    val high = data("年高").toInt
    
    if(price >= high)
      50
    else if(price < high/2)
      50
    else
      0
  }
 
  def score :Int = {
    tdnetScore + edinetScore + highScore
  }
  
  def isGood() :Boolean = {
    score > 100
  }
  
}

object Company{
  def apply(code:String) = {
    new Company(code)
  }
}
