package com.tkido.stock.rss

abstract class CompanyJp(code:String) extends Company(code) {
    
  def makeData :Map[String, String] = {    
    val parsedData = parseProfilePage ++
                     parseConsolidatePage ++
                     parseDetailPage ++
                     parseStockholderPage
    val otherData = makeOtherData
    parsedData ++ otherData
  }
  
  def parseDetailPage :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getOutstanding() :String =
      html.getPreviousLineOf("""<dt class="title">���s�ϊ�����""".r).dropRight(12)
    
    Map("���s" -> getOutstanding)
  }
  
  def parseProfilePage :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code))
    
    def getName() :String = {
      val raw = html.getNextLineOf("""<meta http-equiv="Refresh" content="60">""".r)
      raw.dropRight(27).replaceFirst("""\(��\)""", "")
    }
    def getFeature() :String =
      html.getNextLineOf("""<th width="1%" nowrap>���F</th>""".r).replaceFirst(""" \[��Ɠ��F\]""", "")
    def getConsolidated() :String =
      html.getNextLineOf("""<th nowrap>�A������</th>""".r)
    def getCategory() :String = {
      html.getNextLineOf("""<th nowrap>�Ǝ핪��</th>""".r).replaceAll("��", "").replaceAll("�E", "")
    }
    def getRepresentative() :String = {
      val raw = html.getNextLineOf("""<th nowrap>��\�Җ�</th>""".r)
      raw.replaceAll("�@", "").replaceAll(""" \[����\]""", "")
    }
    def getFoundated() :String =
      html.getNextLineOf("""<th nowrap>�ݗ��N����</th>""".r).slice(0, 4)
    def getListed() :String =
      html.getNextLineOf("""<th nowrap>���N����</th>""".r).slice(0, 4)
    def getSettlement() :String = {
      html.getNextLineOf("""<th nowrap>���Z</th>""".r).replaceAll("����", "").replaceAll(""" \[���Z���@�N��\]""", "")
    }
    def getSingleEmployees() :String =
      html.getNextLineOf("""<th width="1%">�]�ƈ���<br><span class="yjSt">�i�P�Ɓj</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      html.getNextLineOf("""<th width="1%">�]�ƈ���<br><span class="yjSt">�i�A���j</span></th>""".r).dropRight(1)
    def getAge() :String =
      html.getNextLineOf("""<th nowrap>���ϔN��</th>""".r).dropRight(1)
    def getIncome() :String =
      html.getNextLineOf("""<th nowrap>���ϔN��</th>""".r).dropRight(3).replaceAll(",", "")
    
    Map("����" -> getName,
        "���F" -> getFeature,
        "����" -> getConsolidated,
        "����" -> getCategory,
        "�ݗ�" -> getFoundated,
        "���" -> getListed,
        "����" -> getSettlement,
        "�]�A" -> getConsolidatedEmployees,
        "�]�P" -> getSingleEmployees,
        "��"   -> getAge,
        "��"   -> getIncome,
        "��\" -> getRepresentative)
  }
  
  def parseConsolidatePage :Map[String, String] = {
    val html = Html("http://profile.yahoo.co.jp/consolidate/%s".format(code), "EUC-JP")
    
    def getSettlement() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">���Z���\��</td>""".r).replaceFirst("---", "-")
    def getCapitalToAssetRatio() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">���Ȏ��{�䗦</td>""".r).replaceFirst("---", "-")
    def getRoe() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">ROE�i���Ȏ��{���v���j</td>""".r).replaceFirst("---", "-")
    
    Map("���Z" -> getSettlement,
        "��"   -> getCapitalToAssetRatio,
        "ROE"  -> getRoe)
  }
  
  def makeOtherData :Map[String, String] = {
    def getId() :String =
      code
    def getPrice(): String =
      """=IF(H%d=" ", I%d, H%d)"""
    def getCap(): String =
      """=C%d*AD%d/100000"""
    def getEpr(): String =
      """=IF(Y%d=0, 0, 1/Y%d"""
    def getPayoutRatio(): String =
      """=IF(U%d=0, 0, T%d/U%d"""
      
    Map("ID"   -> getId,
        "�l"   -> getPrice,
        "����" -> getCap,
        "�v"   -> getEpr,
        "��"   -> getPayoutRatio)
  }  
  
  def parseStockholderPage :Map[String, String] = {
    val html = Html("http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code))
    
    def getMonth() :String = {
      val rgex = """<tr><th>�����m�茎</th><td>(.*?)</td></tr>""".r
      val opt = html.lines.collectFirst{ case rgex(m) => m }
      if(opt.isDefined)
        Html.removeTags(opt.get.replaceFirst("�����m�茎", "").replaceAll("����", ""))
      else
        ""
    }
    Map("�D��" -> getMonth)
  }  
  
}
object CompanyJp{
  val reJpT = """����.*""".r
  
  def apply(code:String) :CompanyJp = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    html.getNextLineOf("""<dt>%s</dt>""".format(code).r) match {
      case reJpT()    => CompanyJpRss(code)
      case "�}�U�[�Y" => CompanyJpRss(code)
      case _          => CompanyJpOther(code)
    }
  }
}


