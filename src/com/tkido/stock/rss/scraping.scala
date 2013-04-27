package com.tkido.stock.rss

object scraping extends App {
  import scala.io.Source
  import scala.util.matching.Regex
  import java.net.URLEncoder
  import scala.collection.mutable.{Map => MMap}  
  //import com.ibm.icu.text.Transliterator
  
  def removeTags(string:String) :String =
    string.replaceAll("""<.*?>""", "").trim
    
  def getPreviousLineOf(html:Iterable[String], rgex:Regex) :String = {
    var last = ""
    var target = ""
    for(line <- html){
      if(rgex.findFirstIn(line).isDefined)
        target = last
      last = line
    }
    removeTags(target)
  }
  
  def getNextLineOf(html:Iterable[String], rgex:Regex) :String = {
    var flag = false
    var target = ""
    
    for(line <- html){
      if(flag){
        target = line
        flag = false
      }
      if(rgex.findFirstIn(line).isDefined)
        flag = true
    }
    removeTags(target)
  }
  
  def parseDetailPage(code:String) :Map[String, String] = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code)
    val html = Source.fromURL(url, "UTF-8").getLines.toIterable
    
    def getOutstanding() :String =
      getPreviousLineOf(html, """<dt class="title">���s�ϊ�����""".r).dropRight(12)
    def getMarketCode() :String = {
      getNextLineOf(html, """<dt>%s</dt>""".format(code).r) match {
        case "����" => "T"
        case "����1��" => "T"
        case "����2��" => "T"
        case "�}�U�[�Y" => "T"
        case "���1��" => "OS"
        case "���2��" => "OS"
        case "JQG" => "Q"
        case "JQS" => "Q"
        case _ => "X"
      }
    }
    Map("�sID" -> getMarketCode,
        "���s" -> getOutstanding)
  }  
  
  def parseProfilePage(code:String) :Map[String, String] = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code)
    val html = Source.fromURL(url, "UTF-8").getLines.toIterable
    
    def getName() :String = {
      val raw = getNextLineOf(html, """<meta http-equiv="Refresh" content="60">""".r)
      val name = raw.dropRight(27).replaceFirst("""\(��\)""", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFeature() :String =
      getNextLineOf(html, """<th width="1%" nowrap>���F</th>""".r).replaceFirst(""" \[��Ɠ��F\]""", "")
    def getConsolidated() :String =
      getNextLineOf(html, """<th nowrap>�A������</th>""".r)
    def getCategory() :String = {
      val name = getNextLineOf(html, """<th nowrap>�Ǝ핪��</th>""".r).replaceAll("��", "").replaceAll("�E", "")
      """=HYPERLINK("http://kabu-sokuhou.com/brand/item/code___%s/", "%s")""".format(code, name)
    }
    def getRepresentative() :String = {
      val raw = getNextLineOf(html, """<th nowrap>��\�Җ�</th>""".r)
      val name = raw.replaceAll("�@", "").replaceAll(""" \[����\]""", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFoundated() :String =
      getNextLineOf(html, """<th nowrap>�ݗ��N����</th>""".r).slice(0, 4)
    def getListed() :String =
      getNextLineOf(html, """<th nowrap>���N����</th>""".r).slice(0, 4)
    def getSettlement() :String = {
      val name = getNextLineOf(html, """<th nowrap>���Z</th>""".r).replaceAll("����", "").replaceAll(""" \[���Z���@�N��\]""", "")
      """=HYPERLINK("http://www.nikkei.com/markets/company/kigyo/kigyo.aspx?scode=%s", "%s")""".format(code, name)
    }
    def getSingleEmployees() :String =
      getNextLineOf(html, """<th width="1%">�]�ƈ���<br><span class="yjSt">�i�P�Ɓj</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      getNextLineOf(html, """<th width="1%">�]�ƈ���<br><span class="yjSt">�i�A���j</span></th>""".r).dropRight(1)
    def getAge() :String =
      getNextLineOf(html, """<th nowrap>���ϔN��</th>""".r).dropRight(1)
    def getIncome() :String =
      getNextLineOf(html, """<th nowrap>���ϔN��</th>""".r).dropRight(3).replaceAll(",", "")
    
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
  
  def parseConsolidatePage(code:String) :Map[String, String] = {
    val url = "http://profile.yahoo.co.jp/consolidate/%s".format(code)
    val html = Source.fromURL(url, "EUC-JP").getLines.toIterable
    
    def getSettlement() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">���Z���\��</td>""".r).replaceFirst("---", "-")
    def getCapitalToAssetRatio() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">���Ȏ��{�䗦</td>""".r).replaceFirst("---", "-")
    def getRoe() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">ROE�i���Ȏ��{���v���j</td>""".r).replaceFirst("---", "-")
    
    Map("���Z" -> getSettlement,
        "��"   -> getCapitalToAssetRatio,
        "ROE"  -> getRoe)
  }
  
  def parseStockholderPage(code:String) :Map[String, String] = {
    val url = "http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code)
    val html = Source.fromURL(url, "UTF-8").getLines.toIterable
    
    def getMonth() :String = {
      val rgex = """<tr><th>�����m�茎</th><td>(.*?)</td></tr>""".r
      val opt = html.collectFirst{ case rgex(m) => m }
      if(opt.isDefined)
        """=HYPERLINK("%s", "%s")""".format(url, removeTags(opt.get.replaceFirst("�����m�茎", "").replaceAll("����", "")))
      else
        ""
    }
    Map("�D��" -> getMonth)
  }
    
  def makeCodeList() :List[String] = {
    val s = Source.fromFile("data/rss/table.txt", "utf-8")
    val lines = try s.getLines.toList finally s.close
    val codes = lines.map(_.stripLineEnd)
    codes
  }
  
  def makeData(pair:Pair[String, Int]) :Map[String, String] = {
    val (code, row) = pair
    
    val parsedData = parseProfilePage(code) ++
                     parseConsolidatePage(code) ++
                     parseDetailPage(code) ++
                     parseStockholderPage(code)
    val rssData = makeRssData(code, parsedData("�sID"), row)
    val otherData = makeOtherData(code)
    
    parsedData ++ rssData ++ otherData
  }
  
  def makeOtherData(code:String) :Map[String, String] = {
    def getId() :String = {
      """=HYPERLINK("https://kabu.click-sec.com/sec1-3/kabu/meigaraInfo.do?securityCode=%s", "%s")""".format(code, code)
    }    
    Map("ID" -> getId)
  }
  
  def makeRssData(code:String, market:String, row:Int) :Map[String, String] = {
    object DivType extends Enumeration {
      val OUTSTANDING, CURRENT, NONE = Value
    }
    import DivType._
    
    def rssCode(id:String, div:DivType.Value) :String = {
      val divStr = div match {
        case OUTSTANDING => "/Z%d".format(row)
        case CURRENT     => "/C%d".format(row)
        case _ => ""
      }
      /*
      val mainCode = "RSS|'%s.%s'!%s%s".format(code, market, id, divStr)
      div match {
        case CURRENT     => """=IF(C%s=" ", "", %s)""".format(row, mainCode)
        case _ => "=%s".format(mainCode)
      }
      */
      "=RSS|'%s.%s'!%s%s".format(code, market, id, divStr)
    }
    
    Map("���l"     -> rssCode("���ݒl", NONE),
        "�Ŕ�"     -> rssCode("�ŗǔ��C�z�l", NONE),
        "�Ŕ���"   -> rssCode("�ŗǔ��C�z����", NONE),
        "�Ŕ�"     -> rssCode("�ŗǔ��C�z�l", NONE),
        "�Ŕ���"   -> rssCode("�ŗǔ��C�z����", NONE),
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
  
  def makeString(pair:Pair[String, Int]) :String = {
    val data = makeData(pair)
    val order = List("ID", "����", "���l", 
                     "�Ŕ�", "�Ŕ���", "�Ŕ�", "�Ŕ���",
                     "�O��", "�o��",
                     "���c", "���c�T��", "���c", "���c�T��",
                     "�N��", "�N����", "�N��", "�N����",
                     "��", "PER", "PBR", "ROE", 
                     "��", "���Z", "�D��", "����",
                     "���s", "�s", "����", "��\",
                     "���F", "����", "�ݗ�", "���", "����",
                     "�]�A", "�]�P", "��", "��")
    val list = order.map(data(_))
    list.mkString("\t")
  }
  
  val startLine = 2
  val codeList = makeCodeList()
  val codeRowPair = codeList zip Range(startLine, codeList.size+startLine)
  val strings = codeRowPair.map(makeString)
  val result = strings.mkString("\n")
  println(result)
}