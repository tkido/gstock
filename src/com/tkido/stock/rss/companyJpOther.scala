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
        case "Ø1"  => "P"
        case "Ø2"  => "Q"
        case "ØJQS"  => "i"
        case "}U[Y" => "}"
        case "ŒØ1"  => "ŒP"
        case "ŒØ2"  => "ŒQ"
        case "DØ"     => "D"
        case "Ø"     => ""
        case _ => raw
      }
    }
    
    def getCurrentPrice() :String =
      html.getGroupOf("""<td class="stoksPrice">(.*?)</td>""".r)
    def getLastClose() :String =
      html.getPreviousLineOf("""<dt class="title">OúIl""".r).dropRight(7)
    def getRatioLast() :String =
      html.getGroupOf("""<td class="change"><span class="yjSt">Oúä</span><span class=".*? yjMSt">.*?i(.*?)%j</span></td>""".r)
    def getValume() :String =
      html.getPreviousLineOf("""<dt class="title">o""".r).dropRight(8).replaceAll(",", "")
    def getHighest() :String =
      html.getPreviousLineOf("""<dt class="title">Nl""".r).dropRight(10).replaceAll(",", "")
    def getHighestDate() :String =
      html.getPreviousLineOf("""<dt class="title">Nl""".r).takeRight(10).init.tail
    def getLowest() :String =
      html.getPreviousLineOf("""<dt class="title">NÀl""".r).dropRight(10).replaceAll(",", "")
    def getLowestDate() :String =
      html.getPreviousLineOf("""<dt class="title">NÀl""".r).takeRight(10).init.tail
    def getDividendYield() :String =
      html.getPreviousLineOf("""<dt class="title">zñè""".r).replaceFirst("""i.*""", "").replaceFirst("---", "0")
    def getPer() :String =
      html.getPreviousLineOf("""<dt class="title">PER""".r).replaceFirst("""{.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "0")
    def getPbr() :String =
      html.getPreviousLineOf("""<dt class="title">PBR""".r).replaceFirst("""{.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "-")
    
    
    object DivType extends Enumeration {
      val OUTSTANDING, CURRENT, NONE = Value
    }
    import DivType._
    
    def divCode(id:String, div:DivType.Value) :String = {
      val divStr = div match {
        case OUTSTANDING => "/y­sz"
        case CURRENT     => "/ylz"
        case _ => ""
      }
      "=%s%s".format(id, divStr)
    }
    
    Map("»l"     -> getCurrentPrice,
        "Å"     -> "",
        "Å"   -> "",
        "Å"     -> "",
        "Å"   -> "",
        "OI"     -> getLastClose,
        "Oä"     -> getRatioLast,
        "o"     -> divCode(getValume, OUTSTANDING),
        "ú"     -> "",
        "c"     -> "",
        "cT·" -> "",
        "c"     -> "",
        "cT·" -> "",
        "N"     -> divCode(getHighest, CURRENT),
        "Nú"   -> getHighestDate,
        "NÀ"     -> divCode(getLowest, CURRENT),
        "NÀú"   -> getLowestDate,
        "s"       -> getMarketName,
        ""       -> getDividendYield,
        "PER"      -> getPer,
        "PBR"      -> getPbr)
  }
}

object CompanyJpOther{
  def apply(code:String, row:Int) = {
    new CompanyJpOther(code, row)
  }
}


