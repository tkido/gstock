package com.tkido.stock.rss

class CompanyJpRss(code:String) extends CompanyJp(code) {
  override val data = makeData
  
  override def makeData :Map[String, String] = {    
    val rssData = makeRssData
    super.makeData ++ rssData
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


