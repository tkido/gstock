package com.tkido.stock.rss

class CompanyJpOther(code:String, row:Int) extends CompanyJp(code, row) {
  val data = makeData
  
  override def makeData :Map[String, String] = {    
    val rssData = makeNonRssData
    super.makeData ++ rssData
  }
  
  def makeNonRssData :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getMarketName() :String = {
      val raw = html.getNextLineOf("""<dt>%s</dt>""".format(code).r)
      raw match {
        case "東証1部"  => "東１"
        case "東証2部"  => "東２"
        case "東証JQS"  => "東Ｊ"
        case "マザーズ" => "東マ"
        case "名証1部"  => "名１"
        case "名証2部"  => "名２"
        case "札証"     => "札"
        case "福証"     => "福"
        case _ => raw
      }
    }
    
    def getCurrentPrice() :String =
      html.getGroupOf("""<td class="stoksPrice">(.*?)</td>""".r)
    def getLastClose() :String =
      html.getPreviousLineOf("""<dt class="title">前日終値""".r).dropRight(7)
    def getRatioLast() :String =
      html.getGroupOf("""<td class="change"><span class="yjSt">前日比</span><span class=".*? yjMSt">.*?（(.*?)%）</span></td>""".r)
    def getValume() :String =
      html.getPreviousLineOf("""<dt class="title">出来高""".r).dropRight(8).replaceAll(",", "")
    def getHighest() :String =
      html.getPreviousLineOf("""<dt class="title">年初来高値""".r).dropRight(10).replaceAll(",", "")
    def getHighestDate() :String =
      html.getPreviousLineOf("""<dt class="title">年初来高値""".r).takeRight(10).init.tail
    def getLowest() :String =
      html.getPreviousLineOf("""<dt class="title">年初来安値""".r).dropRight(10).replaceAll(",", "")
    def getLowestDate() :String =
      html.getPreviousLineOf("""<dt class="title">年初来安値""".r).takeRight(10).init.tail
    def getDividendYield() :String =
      html.getPreviousLineOf("""<dt class="title">配当利回り""".r).replaceFirst("""（.*""", "").replaceFirst("---", "0")
    def getPer() :String =
      html.getPreviousLineOf("""<dt class="title">PER""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "0")
    def getPbr() :String =
      html.getPreviousLineOf("""<dt class="title">PBR""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "-")
    
    
    object DivType extends Enumeration {
      val OUTSTANDING, CURRENT, NONE = Value
    }
    import DivType._
    
    def divCode(id:String, div:DivType.Value) :String = {
      val divStr = div match {
        case OUTSTANDING => "/【発行】"
        case CURRENT     => "/【値】"
        case _ => ""
      }
      "=%s%s".format(id, divStr)
    }
    
    Map("現値"     -> getCurrentPrice,
        "最売"     -> "",
        "最売数"   -> "",
        "最買"     -> "",
        "最買数"   -> "",
        "前終"     -> getLastClose,
        "前比"     -> getRatioLast,
        "出来"     -> divCode(getValume, OUTSTANDING),
        "落日"     -> "",
        "買残"     -> "",
        "買残週差" -> "",
        "売残"     -> "",
        "売残週差" -> "",
        "年高"     -> divCode(getHighest, CURRENT),
        "年高日"   -> getHighestDate,
        "年安"     -> divCode(getLowest, CURRENT),
        "年安日"   -> getLowestDate,
        "市"       -> getMarketName,
        "利"       -> getDividendYield,
        "PER"      -> getPer,
        "PBR"      -> getPbr)
  }
}

object CompanyJpOther{
  def apply(code:String, row:Int) = {
    new CompanyJpOther(code, row)
  }
}


