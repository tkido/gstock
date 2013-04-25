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
    val html = Source.fromURL(url, "UTF-8").getLines.toIterable
    
    def getName() :String = {
      val raw = getNextLineOf(html, """<meta http-equiv="Refresh" content="60">""".r)
      val name = raw.dropRight(27).replaceFirst("""\(\)""", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFeature() :String =
      getNextLineOf(html, """<th width="1%" nowrap>ÁF</th>""".r).replaceFirst(""" \[éÆÁF\]""", "")
    def getConsolidated() :String =
      getNextLineOf(html, """<th nowrap>AÆ</th>""".r)
    def getCategory() :String = {
      val name = getNextLineOf(html, """<th nowrap>ÆíªÞ</th>""".r).replaceAll("Æ", "").replaceAll("E", "")
      """=HYPERLINK("http://kabu-sokuhou.com/brand/item/code___%s/", "%s")""".format(code, name)
    }
    def getRepresentative() :String = {
      val raw = getNextLineOf(html, """<th nowrap>ã\Ò¼</th>""".r)
      val name = raw.replaceAll("@", "").replaceAll(""" \[ðõ\]""", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFoundated() :String =
      getNextLineOf(html, """<th nowrap>Ý§Nú</th>""".r).slice(0, 4)
    def getListed() :String =
      getNextLineOf(html, """<th nowrap>ãêNú</th>""".r).slice(0, 4)
    def getSettlement() :String = {
      val name = getNextLineOf(html, """<th nowrap>Z</th>""".r).replaceAll("ú", "").replaceAll(""" \[Zîñ@N\]""", "")
      """=HYPERLINK("http://www.nikkei.com/markets/company/kigyo/kigyo.aspx?scode=%s", "%s")""".format(code, name)
    }
    def getSingleEmployees() :String =
      getNextLineOf(html, """<th width="1%">]Æõ<br><span class="yjSt">iPÆj</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      getNextLineOf(html, """<th width="1%">]Æõ<br><span class="yjSt">iAj</span></th>""".r).dropRight(1)
    def getAge() :String =
      getNextLineOf(html, """<th nowrap>½ÏNî</th>""".r).dropRight(1)
    def getIncome() :String =
      getNextLineOf(html, """<th nowrap>½ÏNû</th>""".r).dropRight(3).replaceAll(",", "")
    
    Map("¼Ì" -> getName,
        "ÁF" -> getFeature,
        "Æ" -> getConsolidated,
        "ªÞ" -> getCategory,
        "Ý§" -> getFoundated,
        "ãê" -> getListed,
        "ú" -> getSettlement,
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
      getNextLineOf(html, """<td bgcolor="#ebf4ff">Z­\ú</td>""".r).replaceFirst("---", "-")
    def getCapitalToAssetRatio() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">©È{ä¦</td>""".r).replaceFirst("---", "-")
    def getRoe() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">ROEi©È{v¦j</td>""".r).replaceFirst("---", "-")
    
    Map("Z" -> getSettlement,
        "©"   -> getCapitalToAssetRatio,
        "ROE"  -> getRoe)
  }
  
  def parseStockholderPage(code:String) :Map[String, String] = {
    val url = "http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code)
    val html = Source.fromURL(url, "UTF-8").getLines.toIterable
    
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
    
    val parsedData = parseProfilePage(code) ++
                     parseConsolidatePage(code) ++
                     parseDetailPage(code) ++
                     parseStockholderPage(code)
    val rssData = makeRssData(code, parsedData("sID"), row)
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
        case OUTSTANDING => "/Y%d".format(row)
        case CURRENT     => "/C%d".format(row)
        case _ => ""
      }
      val mainCode = "RSS|'%s.%s'!%s%s".format(code, market, id, divStr)
      div match {
        case CURRENT     => """=IF(C%s=" ", "", %s)""".format(row, mainCode)
        case _ => "=%s".format(mainCode)
      }
    }
    
    Map("»l"     -> rssCode("»Ýl", NONE),
        "Å"     -> rssCode("ÅÇCzl", NONE),
        "Å"   -> rssCode("ÅÇCzÊ", NONE),
        "Å"     -> rssCode("ÅÇCzl", NONE),
        "Å"   -> rssCode("ÅÇCzÊ", NONE),
        "Oä"     -> rssCode("Oúä¦", NONE),
        "o"     -> rssCode("o", OUTSTANDING),
        "c"     -> rssCode("Mpc", OUTSTANDING),
        "cT·" -> rssCode("MpcOTä", OUTSTANDING),
        "c"     -> rssCode("Mpc", OUTSTANDING),
        "cT·" -> rssCode("MpcOTä", OUTSTANDING),
        "N"     -> rssCode("Nl", CURRENT),
        "Nú"   -> rssCode("Nlút", NONE),
        "NÀ"     -> rssCode("NÀl", CURRENT),
        "NÀú"   -> rssCode("NÀlút", NONE),
        "s"       -> rssCode("sêªÌ", NONE),
        ""       -> rssCode("z", CURRENT),
        "PER"      -> rssCode("odq", NONE),
        "PBR"      -> rssCode("oaq", NONE))
  }
  
  def makeString(pair:Pair[String, Int]) :String = {
    val data = makeData(pair)
    val order = List("ID", "¼Ì", "»l", 
                     "Å", "Å", "Å", "Å",
                     "Oä", "o",
                     "c", "cT·", "c", "cT·",
                     "N", "Nú", "NÀ", "NÀú",
                     "", "PER", "PBR", "ROE", 
                     "©", "Z", "DÒ",
                     "­s", "s", "ªÞ", "ã\",
                     "ÁF", "Æ", "Ý§", "ãê", "ú",
                     "]A", "]P", "î", "û")
    val list = order.map(data(_))
    list.mkString("\t")
  }
  
  val startLine = 3
  val codeList = makeCodeList()
  val codeRowPair = codeList zip Range(startLine, codeList.size+startLine)
  val strings = codeRowPair.map(makeString)
  val result = strings.mkString("\n")
  println(result)
}