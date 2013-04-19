package com.tkido.stock.rss

object scraping extends App {
  import scala.io.Source
  import scala.util.matching.Regex
  import java.net.URLEncoder
  import java.nio._
  import java.nio.charset._
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
    removeTags(last)
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
  
  
  def parseProfilePage(code:String) :List[String] = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code)
    val html = HtmlScraper(url)
    
    def getFeature() :String =
      getNextLineOf(html, """<th width="1%" nowrap>���F</th>""".r)
    def getConsolidated() :String =
      getNextLineOf(html, """<th nowrap>�A������</th>""".r)
    def getCategory() :String =
      getNextLineOf(html, """<th nowrap>�Ǝ핪��</th>""".r)
    def getRepresentative() :String = {
      val raw = getNextLineOf(html, """<th nowrap>��\�Җ�</th>""".r)
      val name = raw.replaceAll("�@", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFoundated() :String =
      getNextLineOf(html, """<th nowrap>�ݗ��N����</th>""".r)
    def getListed() :String =
      getNextLineOf(html, """<th nowrap>���N����</th>""".r)
    def getSettlement() :String =
      getNextLineOf(html, """<th nowrap>���Z</th>""".r).dropRight(2)
    def getSingleEmployees() :String =
      getNextLineOf(html, """<th width="1%">�]�ƈ���<br><span class="yjSt">�i�P�Ɓj</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      getNextLineOf(html, """<th width="1%">�]�ƈ���<br><span class="yjSt">�i�A���j</span></th>""".r).dropRight(1)
    def getAge() :String =
      getNextLineOf(html, """<th nowrap>���ϔN��</th>""".r).dropRight(1)
    def getIncome() :String =
      getNextLineOf(html, """<th nowrap>���ϔN��</th>""".r).dropRight(3).replaceAll(",", "")
    
    List(getFeature,
         getConsolidated,
         getCategory,
         getFoundated,
         getListed,
         getSettlement,
         getConsolidatedEmployees,
         getSingleEmployees,
         getAge,
         getIncome,
         getRepresentative)
  }
  
  def parseConsolidatePage(code:String) :List[String] = {
    val url = "http://profile.yahoo.co.jp/consolidate/%s".format(code)
    val html = Source.fromURL(url, "EUC-JP").getLines.toIterable
    
    def getSettlement() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">���Z���\��</td>""".r)
    def getCapitalToAssetRatio() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">���Ȏ��{�䗦</td>""".r)
    def getRoe() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">ROE�i���Ȏ��{���v���j</td>""".r)
    
    List(getSettlement,
         getCapitalToAssetRatio,
         getRoe)
  }
  
  def parseStockholderPage(code:String) :List[String] = {
    val url = "http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code)
    val html = HtmlScraper(url)
    
    def getMonth() :String = {
      val rgex = """<tr><th>�����m�茎</th><td>(.*?)</td></tr>""".r
      val opt = html.collectFirst{ case rgex(m) => m }
      if(opt.isDefined)
        """=HYPERLINK("%s", "%s")""".format(url, removeTags(opt.get.replaceFirst("�����m�茎", "").replaceAll("����", "")))
      else
        ""
    }
    val month = getMonth()
    List(month)
  }
    
  def makeCodeList() :List[String] = {
    val s = Source.fromFile("data/rss/table.txt", "utf-8")
    val lines = try s.getLines.toList finally s.close
    val codes = lines.map(_.stripLineEnd)
    codes
  }
  
  def makeStockString(code:String) :String = {
    val list = parseProfilePage(code) ::: parseConsolidatePage(code) ::: parseStockholderPage(code) 
    //val list = parseProfilePage(code)
    //val list = parseConsolidatePage(code)
    //val list = parseStockholderPage(code)
    //println(list)
    /*
    val buf = new StringBuilder
    val s = "%s.%s".format(code, "")

    buf ++= "\t"
    buf.toString()
    */
    list.mkString("\t")
  }
  
  val codeList = makeCodeList()
  val stockstrings = codeList.map(makeStockString)
  val result = stockstrings.mkString("\n")
  println(result)
}