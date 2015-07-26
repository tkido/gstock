package com.tkido.stock.spider

object SpiderJpHistory {
  import com.tkido.statistics.RankCorrelationIndex
  import com.tkido.tools.Html
  import com.tkido.tools.Logger
  import com.tkido.tools.tryOrElse
  
  def apply(code:String) :Map[String, String] = {
    Logger.debug("SpiderJpHistory Spidering ", code)
    
    def get :Map[String, String] = {
      val html = Html("http://info.finance.yahoo.co.jp/history/?code=%s".format(code))
      
      val reTr = """^</tr><tr.*?>(.*?)</tr></table>$""".r
      val reTd = """^<td>.*?</td><td>(.*)</td>$""".r
      val reSplit = """<tr><td>\d{4}年\d{1,2}月\d{1,2}日</td><td colspan="6" class="through">.*?</td></tr>"""
      val reColor = """ class=".*?""""
      val list = html.getGroupOf(reTr)
                   .replaceAll(reSplit, "")  //exclude stock split information row
                   .replaceAll(reColor, "")  //exclude color
                   .split("""</tr><tr>""").toList.take(21) //about one month 20days + 1day for last close
                   .map(reTd.replaceAllIn(_, m => m.group(1))
                     .split("""</td><td>""")
                     .map(_.replaceAll(",", "").toDouble) )
      
      case class Data(buy:Double, sell:Double, volume:Double, close:Double)
      def toData(arr:Array[Double]) :Data = {
        val (rawOpen, rawHigh, rawLow, rawClose, rawVolume, close, last) = (arr(0), arr(1), arr(2), arr(3), arr(4), arr(5), arr(6))
        val rate = rawClose / close
        
        val open = rawOpen / rate
        val high = rawHigh / rate
        val low = rawLow  / rate
        val volume = rawVolume * rate
        
        val list =
          if(close - open > 0)
            List(last - open, open - low, low - high, high - close)
          else
            List(last - open, open - high, high - low, low - close)
        val buy  = list.filter(_ < 0).sum * -1
        val sell = list.filter(_ > 0).sum
        
        Data(buy, sell, volume, close)
      }
      val data = (list zip list.tail).map(p => p._1 :+ p._2(5) ) //add last day's fixed close.
                   .map(toData)
      
      val volatility = {
        val move  = data.map(d => d.buy + d.sell).sum
        val close = data.map(_.close).sum
        val ratio = move / close
        ratio.toString
      }
      
      val sellingPressureRatio = {
        val buy =
          data.map(d =>
            if(d.buy+d.sell == 0) 0.0
            else d.volume * d.buy / (d.buy+d.sell)
          ).sum
        val sell =
          data.map(d =>
            if(d.buy+d.sell == 0) 0.0
            else d.volume * d.sell / (d.buy+d.sell)
          ).sum
        val ratio = sell * 100 / buy
        ratio.toString + "%"
      }
      
      val rci =
        RankCorrelationIndex(data.map(_.close)).toString + "%"
      
      def getVolumePerDay(span:Int) :String = {
        val volume = data.take(span).map(_.volume).sum / span
        "=%s/%s".format(volume, "【発行】")
      }
      
      Map("SPR"  -> sellingPressureRatio,
          "Vol"  -> volatility,
          "RCI"  -> rci,
          "日出" -> getVolumePerDay(1),
          "週出" -> getVolumePerDay(5),
          "月出" -> getVolumePerDay(20) )
    }
    tryOrElse(get _, Map())
  }
}