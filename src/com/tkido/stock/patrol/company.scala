package com.tkido.stock.patrol

class Company(code:String) {
  import com.tkido.stock.edinet
  import com.tkido.stock.tdnet
  import com.tkido.tools.Html
  import com.tkido.tools.tryOrElse
  
  val fairValue = tryOrElse(makeFairValue _, 0.0)
  def makeFairValue = edinet.Company(code).fairValue.toDouble
  
  val (currentPrice, highest, outStanding, per, pbr) = parseData
  
  def parseData :(Double, Double, Double, Double, Double) = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getCurrentPrice() :Double =
      html.getGroupOf("""<td class="stoksPrice">(.*?)</td>""".r).replaceAll(",", "").toDouble
    val currentPrice = tryOrElse(getCurrentPrice, 0.0)
      
    def getHighest() :Double =
      html.getPreviousLineOf("""<dt class="title">年初来高値""".r).dropRight(10).replaceAll(",", "").replaceFirst("更新", "").toDouble
    val highest = tryOrElse(getHighest, 0.0)
      
    def getOutstanding() :Double =
      html.getPreviousLineOf("""<dt class="title">発行済株式数""".r).dropRight(12).replaceAll(",", "").toDouble * 1000
    val outStanding = tryOrElse(getOutstanding, 0.0)
    
    def getPer() :Double =
      html.getPreviousLineOf("""<dt class="title">PER""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").toDouble
    val per = tryOrElse(getPer, 0.0)
    
    def getPbr() :Double =
      html.getPreviousLineOf("""<dt class="title">PBR""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").toDouble
    val pbr = tryOrElse(getPbr, 0.0)
    
    (currentPrice, highest, outStanding, per, pbr)
  }
  
  def tdnetScore() :Int = {
    if(tdnet.Checker(code))
      50
    else
      0
  }
  
  def edinetScore() :Int = {
    val ratio = (currentPrice * outStanding) / fairValue
    
    if(ratio < 0.3)
      0
    else if(ratio > 1.0)
      0
    else
      ((1.0 - ratio) * 100).toInt
  }
  
   def highScore() :Int = {
    if(currentPrice >= highest * 0.99)
      50
    else if(currentPrice < highest / 2)
      50
    else
      0
  }
 
  def score :Int = {
    tdnetScore + edinetScore + highScore
  }
  
  def isGood() :Boolean = {
    score >= 80
  }
  
}

object Company{
  def apply(code:String) = {
    new Company(code)
  }
}
