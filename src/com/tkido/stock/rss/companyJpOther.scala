package com.tkido.stock.rss

class CompanyJpOther(code:String, row:Int) extends CompanyJp(code, row) {
  import com.tkido.tools.Html
  import com.tkido.tools.tryOrElse
  
  val data = makeData
  
  override def makeData :Map[String, String] = {
    super.makeData ++
    tryOrElse(parseNonRssData _, Map())
  }
  
  def parseNonRssData :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getMarketName() :String = {
      val raw = html.getNextLineOf("""<div class="stockMainTab clearFix">""".r)
      raw match {
        case "東証1部"  => "東１"
        case "東証2部"  => "東２"
        case "東証JQS"  => "東Ｊ"
        case "東証JQG"  => "東Ｊ"
        case "マザーズ" => "東マ"
        case "名証1部"  => "名１"
        case "名証2部"  => "名２"
        case "札証"     => "札"
        case "福証"     => "福"
        case _          => raw
      }
    }
    
    def getCurrentPrice() :String =
      html.getGroupOf("""^.*?<td class="stoksPrice">(.*?)</td>""".r).replaceFirst("---", " ")
    def getLastClose() :String =
      html.getPreviousLineOf("""<dt class="title">前日終値""".r).dropRight(7)
    def getRatioLast() :String =
      html.getGroupOf("""<td class="change"><span class="yjSt">前日比</span><span class=".*? yjMSt">.*?（(.*?)%）</span></td>""".r)
    def getValume() :String =
      html.getPreviousLineOf("""<dt class="title">出来高""".r).dropRight(8).replaceAll(",", "").replaceFirst("-", "0")
    def getHighest() :String =
      html.getPreviousLineOf("""<dt class="title">年初来高値""".r).dropRight(10).replaceAll(",", "").replaceFirst("更新", "")
    def getHighestDate() :String =
      html.getPreviousLineOf("""<dt class="title">年初来高値""".r).takeRight(10).init.tail
    def getLowest() :String =
      html.getPreviousLineOf("""<dt class="title">年初来安値""".r).dropRight(10).replaceAll(",", "").replaceFirst("更新", "")
    def getLowestDate() :String =
      html.getPreviousLineOf("""<dt class="title">年初来安値""".r).takeRight(10).init.tail
    def getDividendYield() :String =
      html.getPreviousLineOf("""<dt class="title">配当利回り""".r).replaceFirst("""（.*""", "").replaceFirst("---", "0")
    def getPer() :String =
      html.getPreviousLineOf("""<dt class="title">PER""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "0")
    def getPbr() :String =
      html.getPreviousLineOf("""<dt class="title">PBR""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "-")
    def getBuyOnCredit =
      html.getPreviousLineOf("""<dt class="title">信用買残""".r).replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-")
    def getSellOnCredit =
      html.getPreviousLineOf("""<dt class="title">信用売残""".r).replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-")
    def getBuyOnCreditDelta =
      html.getPreviousLineOf("""<dt class="title"><span class="icoL">前週比</span>.*?shinyoubaizann_zensyuuhi""".r).replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-")
    def getSellOnCreditDelta =
      html.getPreviousLineOf("""<dt class="title"><span class="icoL">前週比</span>.*?shinyouuriage_zensyuuhi""".r).replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-")
    
    def divCode(content:String, div:String) :String =
      if(content == "-") "-" else "=%s/%s".format(content, div)
    
    Map("現値"     -> getCurrentPrice,
        "前終"     -> getLastClose,
        "前比"     -> getRatioLast,
        "出来"     -> divCode(getValume, "【発行】"),
        "買残"     -> divCode(getBuyOnCredit, "【発行】"),
        "買残週差" -> divCode(getBuyOnCreditDelta, "【発行】"),
        "売残"     -> divCode(getSellOnCredit, "【発行】"),
        "売残週差" -> divCode(getSellOnCreditDelta, "【発行】"),
        "年高"     -> divCode(getHighest, "【値】"),
        "年高日"   -> getHighestDate,
        "年安"     -> divCode(getLowest, "【値】"),
        "年安日"   -> getLowestDate,
        "市"       -> getMarketName,
        "利"       -> getDividendYield,
        "PER"      -> getPer,
        "PBR"      -> getPbr )
  }
}

object CompanyJpOther{
  def apply(code:String, row:Int) = {
    new CompanyJpOther(code, row)
  }
}


