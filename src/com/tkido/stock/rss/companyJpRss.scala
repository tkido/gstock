package com.tkido.stock.rss

class CompanyJpRss(code:String, row:Int) extends CompanyJp(code, row) {
  override val data = makeData
  
  override def makeData :Map[String, String] =    
    super.makeData ++ makeRssData
  
  def makeRssData :Map[String, String] = {
    def rssCode(id:String, div:String) :String = {
      val divStr = if(div == "") "" else "/%s".format(div)
      "=RSS|'%s.T'!%s%s".format(code, id, divStr)
    }
    
    Map("���l"     -> rssCode("���ݒl", ""),
        "�Ŕ�"     -> rssCode("�ŗǔ��C�z�l", ""),
        "�Ŕ���"   -> rssCode("�ŗǔ��C�z����", ""),
        "�Ŕ�"     -> rssCode("�ŗǔ��C�z�l", ""),
        "�Ŕ���"   -> rssCode("�ŗǔ��C�z����", ""),
        "�O�I"     -> rssCode("�O���I�l", ""),
        "�O��"     -> rssCode("�O���䗦", ""),
        "�o��"     -> rssCode("�o����", "�y���s�z"),
        "����"     -> rssCode("�z������", ""),
        "���c"     -> rssCode("�M�p���c", "�y���s�z"),
        "���c�T��" -> rssCode("�M�p���c�O�T��", "�y���s�z"),
        "���c"     -> rssCode("�M�p���c", "�y���s�z"),
        "���c�T��" -> rssCode("�M�p���c�O�T��", "�y���s�z"),
        "�N��"     -> rssCode("�N�������l", "�y�l�z"),
        "�N����"   -> rssCode("�N�������l���t", ""),
        "�N��"     -> rssCode("�N�������l", "�y�l�z"),
        "�N����"   -> rssCode("�N�������l���t", ""),
        "�s"       -> rssCode("�s�ꕔ����", ""),
        "��"       -> rssCode("�z��", "�y�l�z"),
        "PER"      -> rssCode("�o�d�q", ""),
        "PBR"      -> rssCode("�o�a�q", ""),
        "R"        -> "R" )
  }
}

object CompanyJpRss{
  def apply(code:String, row:Int) = {
    new CompanyJpRss(code, row)
  }
}


