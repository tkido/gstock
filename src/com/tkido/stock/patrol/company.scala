package com.tkido.stock.patrol

import com.tkido.stock.edinet
import com.tkido.stock.tdnet
import com.tkido.tools.Html
import com.tkido.tools.Search
import com.tkido.tools.tryOrElse

class Company(code:String) {
  val fairValue = tryOrElse(makeFairValue _, 0.0)
  def makeFairValue = edinet.Company(code).fairValue.toDouble
  
  val (currentPrice, highest, outStanding, per, pbr) = parseData
  
  def parseData :(Double, Double, Double, Double, Double) = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    val map = html.search(List(
      Search("currentPrice", """<td class="stoksPrice">(.*?)</td>""".r, Search.GROUP, _.replaceAll(",", "")),
      Search("highest", """<dt class="title">年初来高値""".r, Search.LAST, _.dropRight(10).replaceAll(",", "").replaceFirst("更新", "")),
      Search("outStanding", """<dt class="title">発行済株式数""".r, Search.LAST, _.dropRight(12).replaceAll(",", "")),
      Search("per", """<dt class="title">PER""".r, Search.LAST, _.replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "")),
      Search("pbr", """<dt class="title">PBR""".r, Search.LAST, _.replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "")) )
    )

    val currentPrice = map.getOrElse("currentPrice", "0.0").toDouble
    val highest = map.getOrElse("highest", "0.0").toDouble
    val outStanding = map.getOrElse("outStanding", "0.0").toDouble * 1000
    val per = map.getOrElse("per", "0.0").toDouble
    val pbr = map.getOrElse("pbr", "0.0").toDouble
    
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
    tryOrElse(tdnetScore, 0) + edinetScore + highScore
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
