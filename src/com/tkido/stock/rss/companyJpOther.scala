package com.tkido.stock.rss

class CompanyJpOther(code:String, row:Int) extends CompanyJp(code, row) {
  val data = makeData
  
  override def makeData :Map[String, String] =
    super.makeData ++ makeNonRssData
  
  def makeNonRssData :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getMarketName() :String = {
      val raw = html.getNextLineOf("""<dt>%s</dt>""".format(code).r)
      raw match {
        case "����1��"  => "���P"
        case "����2��"  => "���Q"
        case "����JQS"  => "���i"
        case "����JQG"  => "���i"
        case "�}�U�[�Y" => "���}"
        case "����1��"  => "���P"
        case "����2��"  => "���Q"
        case "�D��"     => "�D"
        case "����"     => "��"
        case _ => raw
      }
    }
    
    def getCurrentPrice() :String =
      html.getGroupOf("""<td class="stoksPrice">(.*?)</td>""".r).replaceFirst("---", " ")
    def getLastClose() :String =
      html.getPreviousLineOf("""<dt class="title">�O���I�l""".r).dropRight(7)
    def getRatioLast() :String =
      html.getGroupOf("""<td class="change"><span class="yjSt">�O����</span><span class=".*? yjMSt">.*?�i(.*?)%�j</span></td>""".r)
    def getValume() :String =
      html.getPreviousLineOf("""<dt class="title">�o����""".r).dropRight(8).replaceAll(",", "").replaceFirst("-", "0")
    def getHighest() :String =
      html.getPreviousLineOf("""<dt class="title">�N�������l""".r).dropRight(10).replaceAll(",", "").replaceFirst("�X�V", "")
    def getHighestDate() :String =
      html.getPreviousLineOf("""<dt class="title">�N�������l""".r).takeRight(10).init.tail
    def getLowest() :String =
      html.getPreviousLineOf("""<dt class="title">�N�������l""".r).dropRight(10).replaceAll(",", "").replaceFirst("�X�V", "")
    def getLowestDate() :String =
      html.getPreviousLineOf("""<dt class="title">�N�������l""".r).takeRight(10).init.tail
    def getDividendYield() :String =
      html.getPreviousLineOf("""<dt class="title">�z�������""".r).replaceFirst("""�i.*""", "").replaceFirst("---", "0")
    def getPer() :String =
      html.getPreviousLineOf("""<dt class="title">PER""".r).replaceFirst("""�{.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "0")
    def getPbr() :String =
      html.getPreviousLineOf("""<dt class="title">PBR""".r).replaceFirst("""�{.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "-")
    def getBuyOnCredit =
      html.getPreviousLineOf("""<dt class="title">�M�p���c""".r).replaceFirst("""��.*""", "").replaceAll(",", "").replaceFirst("---", "-")
    def getSellOnCredit =
      html.getPreviousLineOf("""<dt class="title">�M�p���c""".r).replaceFirst("""��.*""", "").replaceAll(",", "").replaceFirst("---", "-")
    def getBuyOnCreditDelta =
      html.getPreviousLineOf("""<dt class="title"><span class="icoL">�O�T��</span>.*?shinyoubaizann_zensyuuhi""".r).replaceFirst("""��.*""", "").replaceAll(",", "").replaceFirst("---", "-")
    def getSellOnCreditDelta =
      html.getPreviousLineOf("""<dt class="title"><span class="icoL">�O�T��</span>.*?shinyouuriage_zensyuuhi""".r).replaceFirst("""��.*""", "").replaceAll(",", "").replaceFirst("---", "-")
    
    def divCode(content:String, div:String) :String =
      if(content == "-") "-" else "=%s/%s".format(content, div)
    
    Map("���l"     -> getCurrentPrice,
        "�O�I"     -> getLastClose,
        "�O��"     -> getRatioLast,
        "�o��"     -> divCode(getValume, "�y���s�z"),
        "���c"     -> divCode(getBuyOnCredit, "�y���s�z"),
        "���c�T��" -> divCode(getBuyOnCreditDelta, "�y���s�z"),
        "���c"     -> divCode(getSellOnCredit, "�y���s�z"),
        "���c�T��" -> divCode(getSellOnCreditDelta, "�y���s�z"),
        "�N��"     -> divCode(getHighest, "�y�l�z"),
        "�N����"   -> getHighestDate,
        "�N��"     -> divCode(getLowest, "�y�l�z"),
        "�N����"   -> getLowestDate,
        "�s"       -> getMarketName,
        "��"       -> getDividendYield,
        "PER"      -> getPer,
        "PBR"      -> getPbr )
  }
}

object CompanyJpOther{
  def apply(code:String, row:Int) = {
    new CompanyJpOther(code, row)
  }
}


