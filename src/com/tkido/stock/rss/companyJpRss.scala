package com.tkido.stock.rss

class CompanyJpRss(code:String) extends CompanyJp(code) {
  println("CompanyJpRss:%s".format(code))
  val data = makeData
  
  def makeData :Map[String, String] = {    
    val parsedData = parseProfilePage ++
                     parseConsolidatePage ++
                     parseDetailPage ++
                     parseStockholderPage
    val otherData = makeOtherData
    val rssData = makeRssData
    
    parsedData ++ otherData ++ rssData
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
  
  def makeRssData :Map[String, String] = {
    object DivType extends Enumeration {
      val OUTSTANDING, CURRENT, NONE = Value
    }
    import DivType._
    
    def rssCode(id:String, div:DivType.Value) :String = {
      val divStr = div match {
        case OUTSTANDING => "/AD%d"
        case CURRENT     => "/C%d"
        case _ => ""
      }
      "=RSS|'%s.T'!%s%s".format(code, id, divStr)
    }
    
    Map("���l"     -> rssCode("���ݒl", NONE),
        "�Ŕ�"     -> rssCode("�ŗǔ��C�z�l", NONE),
        "�Ŕ���"   -> rssCode("�ŗǔ��C�z����", NONE),
        "�Ŕ�"     -> rssCode("�ŗǔ��C�z�l", NONE),
        "�Ŕ���"   -> rssCode("�ŗǔ��C�z����", NONE),
        "�O�I"     -> rssCode("�O���I�l", NONE),
        "�O��"     -> rssCode("�O���䗦", NONE),
        "�o��"     -> rssCode("�o����", OUTSTANDING),
        "����"     -> rssCode("�z������", NONE),
        "���c"     -> rssCode("�M�p���c", OUTSTANDING),
        "���c�T��" -> rssCode("�M�p���c�O�T��", OUTSTANDING),
        "���c"     -> rssCode("�M�p���c", OUTSTANDING),
        "���c�T��" -> rssCode("�M�p���c�O�T��", OUTSTANDING),
        "�N��"     -> rssCode("�N�������l", CURRENT),
        "�N����"   -> rssCode("�N�������l���t", NONE),
        "�N��"     -> rssCode("�N�������l", CURRENT),
        "�N����"   -> rssCode("�N�������l���t", NONE),
        "�s"       -> rssCode("�s�ꕔ����", NONE),
        "��"       -> rssCode("�z��", CURRENT),
        "PER"      -> rssCode("�o�d�q", NONE),
        "PBR"      -> rssCode("�o�a�q", NONE))
  }
}

object CompanyJpRss{
  def apply(code:String) = {
    new CompanyJpRss(code)
  }
}


