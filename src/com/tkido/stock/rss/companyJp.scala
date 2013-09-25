package com.tkido.stock.rss

abstract class CompanyJp(code:String, row:Int) extends Company(code, row) {
  import com.tkido.stock.Config
  import com.tkido.tools.Html
  
  def makeData :Map[String, String] = {
    val parsedData = try{
      parseProfile ++ parseConsolidate ++ parseDetail ++ parseStockholder ++ parseHistory
    }catch{
      case _ => Map()
    }
    parsedData ++ makeOtherData
  }
  
  def parseDetail :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getOutstanding() :String =
      html.getPreviousLineOf("""<dt class="title">発行済株式数""".r).dropRight(12)
    
    Map("発行" -> getOutstanding)
  }
  
  def parseProfile :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code))
    
    def getName() :String = {
      val raw = html.getNextLineOf("""<meta http-equiv="Refresh" content="60">""".r)
      raw.dropRight(27).replaceFirst("""\(株\)""", "")
    }
    def getFeature() :String =
      html.getNextLineOf("""<th width="1%" nowrap>特色</th>""".r).replaceFirst(""" \[企業特色\]""", "")
    def getConsolidated() :String =
      html.getNextLineOf("""<th nowrap>連結事業</th>""".r)
    def getCategory() :String = {
      html.getNextLineOf("""<th nowrap>業種分類</th>""".r).replaceAll("業", "").replaceAll("・", "")
    }
    def getRepresentative() :String = {
      val raw = html.getNextLineOf("""<th nowrap>代表者名</th>""".r)
      raw.replaceAll("　", "").replaceAll(""" \[役員\]""", "")
    }
    def getFoundated() :String =
      html.getNextLineOf("""<th nowrap>設立年月日</th>""".r).slice(0, 4)
    def getListed() :String =
      html.getNextLineOf("""<th nowrap>上場年月日</th>""".r).slice(0, 4)
    def getSettlement() :String = {
      html.getNextLineOf("""<th nowrap>決算</th>""".r).replaceAll("末日", "").replaceAll(""" \[決算情報　年次\]""", "")
    }
    def getSingleEmployees() :String =
      html.getNextLineOf("""<th width="1%">従業員数<br><span class="yjSt">（単独）</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      html.getNextLineOf("""<th width="1%">従業員数<br><span class="yjSt">（連結）</span></th>""".r).dropRight(1)
    def getAge() :String =
      html.getNextLineOf("""<th nowrap>平均年齢</th>""".r).dropRight(1)
    def getIncome() :String =
      html.getNextLineOf("""<th nowrap>平均年収</th>""".r).dropRight(3).replaceAll(",", "")
    
    Map("名称" -> getName,
        "特色" -> getFeature,
        "事業" -> getConsolidated,
        "分類" -> getCategory,
        "設立" -> getFoundated,
        "上場" -> getListed,
        "決期" -> getSettlement,
        "従連" -> getConsolidatedEmployees,
        "従単" -> getSingleEmployees,
        "齢"   -> getAge,
        "収"   -> getIncome,
        "代表" -> getRepresentative)
  }
  
  def parseConsolidate :Map[String, String] = {
    val html = Html("http://profile.yahoo.co.jp/consolidate/%s".format(code), "EUC-JP")
    
    def getSettlement() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">決算発表日</td>""".r).replaceFirst("---", "-")
    def getCapitalToAssetRatio() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">自己資本比率</td>""".r).replaceFirst("---", "-")
    def getRoe() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">ROE（自己資本利益率）</td>""".r).replaceFirst("---", "-")
    
    Map("決算" -> getSettlement,
        "自"   -> getCapitalToAssetRatio,
        "ROE"  -> getRoe)
  }
  
  def parseStockholder :Map[String, String] = {
    val html = Html("http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code))
    
    def getMonth() :String =
      html.getGroupOf("""<tr><th>権利確定月</th><td>(.*?)</td></tr>""".r).replaceAll("末日", "")
    
    Map("優待" -> getMonth)
  }
  
  def parseHistory :Map[String, String] = {
    val html = Html("http://info.finance.yahoo.co.jp/history/?code=%s".format(code))
    
    def getSellingPressureRatio() :String = {
      val reTr = """^</tr><tr.*?>(.*?)</tr></table>$""".r
      val reTd = """^<td>.*?</td><td>(.*)</td><td>.*?</td>$""".r
      val arr = html.getGroupOf(reTr).replaceAll(""" class=".*?"""", "").split("""</tr><tr>""")
                  .take(21)  //about one month
                  .map(reTd.replaceAllIn(_, m => m.group(1))
                    .split("""</td><td>""")
                    .map(_.replaceAll(",", "").toLong) )
      val data = (arr zip arr.tail).map(p => p._1 :+ p._2(3) )  //add last day's closing price.
      
      def arrToBuySellPair(arr:Array[Long]) :Pair[Long, Long] = {
        val (open, high, low, close, volume, last) = (arr(0), arr(1), arr(2), arr(3), arr(4), arr(5))
        val list =
          if(close - open > 0)
            List(last - open, open - low, low - high, high - close)
          else
            List(last - open, open - high, high - low, low - close)
        val buy  = list.filter(_ < 0).sum * -1
        val sell = list.filter(_ > 0).sum
        if(buy+sell == 0)
          0L -> 0L
        else
          volume * buy / (buy+sell) -> volume * sell / (buy+sell)
      }
      val pairs = data.map(arrToBuySellPair)
      val buy  = pairs.map(_._1).sum
      val sell = pairs.map(_._2).sum
      val ratio = sell * 100 / buy
      ratio.toString + "%"
    }
    Map("SPR" -> getSellingPressureRatio)
  }
  
}
object CompanyJp{
  import com.tkido.stock.Config
  import com.tkido.tools.Html
  
  val reJpT = """東証.*""".r
  
  def apply(code:String, row:Int) :CompanyJp = {
    if(!Config.rssFlag || row > 300) return CompanyJpOther(code, row)
    try{
      val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
      html.getNextLineOf("""<dt>%s</dt>""".format(code).r) match {
        case reJpT()    => CompanyJpRss(code, row)
        case "マザーズ" => CompanyJpRss(code, row)
        case _          => CompanyJpOther(code, row)
      }
    }catch{
      case _ => CompanyJpOther(code, row)
    }
  }
}


