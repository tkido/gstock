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
    val html = HtmlScraper(url)
    
    def getOutstanding() :String =
      getPreviousLineOf(html, """<dt class="title">­sÏ®""".r).dropRight(12)
    def getMarketCode() :String = {
      getNextLineOf(html, """<dt>%s</dt>""".format(code).r) match {
        case "Ø" => "T"
        case "Ø1" => "T"
        case "Ø2" => "T"
        case "}U[Y" => "T"
        case "åØ1" => "OS"
        case "åØ2" => "OS"
        case "JQG" => "Q"
        case "JQS" => "Q"
        case _ => "X"
      }
    }
    Map("sID" -> getMarketCode,
        "­s" -> getOutstanding)
  }  
  
  def parseProfilePage(code:String) :Map[String, String] = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code)
    val html = HtmlScraper(url)
    
    def getFeature() :String =
      getNextLineOf(html, """<th width="1%" nowrap>ÁF</th>""".r)
    def getConsolidated() :String =
      getNextLineOf(html, """<th nowrap>AÆ</th>""".r)
    def getCategory() :String =
      getNextLineOf(html, """<th nowrap>ÆíªÞ</th>""".r)
    def getRepresentative() :String = {
      val raw = getNextLineOf(html, """<th nowrap>ã\Ò¼</th>""".r)
      val name = raw.replaceAll("@", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFoundated() :String =
      getNextLineOf(html, """<th nowrap>Ý§Nú</th>""".r).slice(0, 4)
    def getListed() :String =
      getNextLineOf(html, """<th nowrap>ãêNú</th>""".r).slice(0, 4)
    def getSettlement() :String =
      getNextLineOf(html, """<th nowrap>Z</th>""".r).dropRight(2)
    def getSingleEmployees() :String =
      getNextLineOf(html, """<th width="1%">]Æõ<br><span class="yjSt">iPÆj</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      getNextLineOf(html, """<th width="1%">]Æõ<br><span class="yjSt">iAj</span></th>""".r).dropRight(1)
    def getAge() :String =
      getNextLineOf(html, """<th nowrap>½ÏNî</th>""".r).dropRight(1)
    def getIncome() :String =
      getNextLineOf(html, """<th nowrap>½ÏNû</th>""".r).dropRight(3).replaceAll(",", "")
    
    Map("ÁF" -> getFeature,
        "AÆ" -> getConsolidated,
        "Æ±" -> getCategory,
        "Ý§" -> getFoundated,
        "ãê" -> getListed,
        ""   -> getSettlement,
        "]A" -> getConsolidatedEmployees,
        "]P" -> getSingleEmployees,
        "î"   -> getAge,
        "û"   -> getIncome,
        "ã\" -> getRepresentative)
  }
  
  def parseConsolidatePage(code:String) :Map[String, String] = {
    val url = "http://profile.yahoo.co.jp/consolidate/%s".format(code)
    val html = Source.fromURL(url, "EUC-JP").getLines.toIterable
    
    def getSettlement() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">Z­\ú</td>""".r)
    def getCapitalToAssetRatio() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">©È{ä¦</td>""".r)
    def getRoe() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">ROEi©È{v¦j</td>""".r)
    
    Map("O" -> getSettlement,
        "©" -> getCapitalToAssetRatio,
        "ROE" -> getRoe)
  }
  
  def parseStockholderPage(code:String) :Map[String, String] = {
    val url = "http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code)
    val html = HtmlScraper(url)
    
    def getMonth() :String = {
      val rgex = """<tr><th> mè</th><td>(.*?)</td></tr>""".r
      val opt = html.collectFirst{ case rgex(m) => m }
      if(opt.isDefined)
        """=HYPERLINK("%s", "%s")""".format(url, removeTags(opt.get.replaceFirst(" mè", "").replaceAll("ú", "")))
      else
        ""
    }
    Map("DÒ" -> getMonth)
  }
    
  def makeCodeList() :List[String] = {
    val s = Source.fromFile("data/rss/table.txt", "utf-8")
    val lines = try s.getLines.toList finally s.close
    val codes = lines.map(_.stripLineEnd)
    codes
  }
  
  def makeData(pair:Pair[String, Int]) :Map[String, String] = {
    val (code, row) = pair
    //val list = parseProfilePage(code) ::: parseConsolidatePage(code) ::: parseStockholderPage(code) 
    val parsedData = parseDetailPage(code) ++
                     parseStockholderPage(code)
               
    //val data = parseProfilePage(code)
    //val data = parseConsolidatePage(code)
    //val data = parseStockholderPage(code)
    val pushedData = pushData(code, parsedData("sID"), row)
    val data = parsedData ++ pushedData
    data
  }
  
  def pushData(code:String, market:String, row:Int) :Map[String, String] = {
    def rssCode(id:String, div:String) :String = {
      val divStr = div match {
        case "O" => "/T%d".format(row)
        case "C" => "/C%d".format(row)
        case _   => ""
      }
      "=RSS|'%s.%s'!%s%s".format(code, market, id, divStr)
    }
    
    Map("ID"       -> code,
        "¼Ì"     -> rssCode("Á¿¼Ì", ""),
        "»l"     -> rssCode("»Ýl", ""),
        "Oä"     -> rssCode("Oúä¦", ""),
        "o"     -> rssCode("o", "O"),
        "c"     -> rssCode("Mpc", "O"),
        "cT·" -> rssCode("MpcOTä", "O"),
        "c"     -> rssCode("Mpc", "O"),
        "cT·" -> rssCode("MpcOTä", "O"),
        "N"     -> rssCode("Nl", "C"),
        "Nú"   -> rssCode("Nlút", ""),
        "NÀ"     -> rssCode("NÀl", "C"),
        "NÀú"   -> rssCode("NÀlút", ""),
        "s"       -> rssCode("sêªÌ", ""),
        ""       -> rssCode("z", "C"),
        "PER"      -> rssCode("odq", "C"),
        "PBR"      -> rssCode("oaq", "C"))
  }
  
  def makeString(pair:Pair[String, Int]) :String = {
    val data = makeData(pair)
    val order = List("ID", "­s", "DÒ", "", "o")
    val list = order.map(data(_))
    list.mkString("\t")
  }
  
  val codeList = makeCodeList()
  val codeAndNum = codeList zip Range(2, codeList.size+2)
  val strings = codeAndNum.map(makeString)
  val result = strings.mkString("\n")
  println(result)
}