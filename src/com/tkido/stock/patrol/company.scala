package com.tkido.stock.patrol

import com.tkido.stock.edinet
import com.tkido.stock.tdnet
import com.tkido.tools.Html
import com.tkido.tools.Search
import com.tkido.tools.retry
import com.tkido.tools.tryOrElse

object Company{
  val rule = List(
    Search("currentPrice", """<td class="stoksPrice">(.*?)</td>""".r, Search.GROUP, _.replaceAll(",", "")),
    Search("highest", """<dt class="title">年初来高値""".r, Search.LAST, _.dropRight(10).replaceAll(",", "").replaceFirst("更新", "")),
    Search("outStanding", """<dt class="title">発行済株式数""".r, Search.LAST, _.dropRight(12).replaceAll(",", "")),
    Search("per", """<dt class="title">PER""".r, Search.LAST, _.replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "")),
    Search("pbr", """<dt class="title">PBR""".r, Search.LAST, _.replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "")) )
  
  def apply(code:String) :Boolean = {
    
    def isGood(html:Html) :Boolean = {
      val map = html.search(rule)
      
      val currentPrice = tryOrElse(() => map("currentPrice").toDouble, 0.0)
      val highest = tryOrElse(() => map("highest").toDouble, 0.0)
      val outStanding = tryOrElse(() => map("outStanding").toDouble, 0.0)
      val per = tryOrElse(() => map("per").toDouble, 0.0)
      val pbr = tryOrElse(() => map("pbr").toDouble, 0.0)
      
      def makeFairValue = edinet.Company(code).fairValue.toDouble
      val fairValue = tryOrElse(makeFairValue _, 0.0)
      
      def tdnetScore() :Int = {
        if(tdnet.Checker(code))
          50
        else
          0
      }
      
      def edinetScore() :Int = {
        val ratio = (currentPrice * outStanding * 1000) / fairValue
        
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
      
      score >= 80
    }
    
    retry { Html(s"https://stocks.finance.yahoo.co.jp/stocks/detail/?code=${code}") } match {
      case Some(html) => isGood(html)
      case None       => false
    }
  }
}
