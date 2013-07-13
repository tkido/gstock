package com.tkido.stock.rss

object scraping extends App {
  import scala.io.Source
  import java.io.PrintWriter
  import scala.collection.mutable.{Map => MMap}
  
  def parseDetailPage(code:String) :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getOutstanding() :String =
      html.getPreviousLineOf("""<dt class="title">­sÏ®""".r).dropRight(12)
    def getMarketCode() :String = {
      html.getNextLineOf("""<dt>%s</dt>""".format(code).r) match {
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
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code))
    
    def getName() :String = {
      val raw = html.getNextLineOf("""<meta http-equiv="Refresh" content="60">""".r)
      raw.dropRight(27).replaceFirst("""\(\)""", "")
    }
    def getFeature() :String =
      html.getNextLineOf("""<th width="1%" nowrap>ÁF</th>""".r).replaceFirst(""" \[éÆÁF\]""", "")
    def getConsolidated() :String =
      html.getNextLineOf("""<th nowrap>AÆ</th>""".r)
    def getCategory() :String = {
      html.getNextLineOf("""<th nowrap>ÆíªÞ</th>""".r).replaceAll("Æ", "").replaceAll("E", "")
    }
    def getRepresentative() :String = {
      val raw = html.getNextLineOf("""<th nowrap>ã\Ò¼</th>""".r)
      raw.replaceAll("@", "").replaceAll(""" \[ðõ\]""", "")
    }
    def getFoundated() :String =
      html.getNextLineOf("""<th nowrap>Ý§Nú</th>""".r).slice(0, 4)
    def getListed() :String =
      html.getNextLineOf("""<th nowrap>ãêNú</th>""".r).slice(0, 4)
    def getSettlement() :String = {
      html.getNextLineOf("""<th nowrap>Z</th>""".r).replaceAll("ú", "").replaceAll(""" \[Zîñ@N\]""", "")
    }
    def getSingleEmployees() :String =
      html.getNextLineOf("""<th width="1%">]Æõ<br><span class="yjSt">iPÆj</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      html.getNextLineOf("""<th width="1%">]Æõ<br><span class="yjSt">iAj</span></th>""".r).dropRight(1)
    def getAge() :String =
      html.getNextLineOf("""<th nowrap>½ÏNî</th>""".r).dropRight(1)
    def getIncome() :String =
      html.getNextLineOf("""<th nowrap>½ÏNû</th>""".r).dropRight(3).replaceAll(",", "")
    
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
    val html = Html("http://profile.yahoo.co.jp/consolidate/%s".format(code), "EUC-JP")
    
    def getSettlement() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">Z­\ú</td>""".r).replaceFirst("---", "-")
    def getCapitalToAssetRatio() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">©È{ä¦</td>""".r).replaceFirst("---", "-")
    def getRoe() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">ROEi©È{v¦j</td>""".r).replaceFirst("---", "-")
    
    Map("Z" -> getSettlement,
        "©"   -> getCapitalToAssetRatio,
        "ROE"  -> getRoe)
  }
  
  def parseStockholderPage(code:String) :Map[String, String] = {
    val html = Html("http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code))
    
    def getMonth() :String = {
      val rgex = """<tr><th> mè</th><td>(.*?)</td></tr>""".r
      val opt = html.lines.collectFirst{ case rgex(m) => m }
      if(opt.isDefined)
        Html.removeTags(opt.get.replaceFirst(" mè", "").replaceAll("ú", ""))
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
  
  def writeFile(data: String) {
    val out = new PrintWriter("data/rss/result.txt")
    out.println(data)
    out.close
  }
  
  def makeData(pair:Pair[String, Int]) :Map[String, String] = {
    val (code, row) = pair
    
    val parsedData = parseProfilePage(code) ++
                     parseConsolidatePage(code) ++
                     parseDetailPage(code) ++
                     parseStockholderPage(code)
    val rssData = makeRssData(code, parsedData("sID"), row)
    val otherData = makeOtherData(code, row)
    
    parsedData ++ rssData ++ otherData
  }
  
  def makeOtherData(code:String, row:Int) :Map[String, String] = {
    def getId() :String =
      code
    def getPrice(): String =
      """=IF(H%d=" ", I%d, H%d)""".format(row, row, row)
    def getCap(): String =
      """=C%d*AD%d/100000""".format(row, row)
    def getEpr(): String =
      """=IF(Y%d=0, 0, 1/Y%d""".format(row, row)
    def getPayoutRatio(): String =
      """=IF(U%d=0, 0, T%d/U%d""".format(row, row, row)
      
    Map("ID"   -> getId,
        "l"   -> getPrice,
        "¿" -> getCap,
        "v"   -> getEpr,
        "«"   -> getPayoutRatio)
  }
  
  def makeRssData(code:String, market:String, row:Int) :Map[String, String] = {
    object DivType extends Enumeration {
      val OUTSTANDING, CURRENT, NONE = Value
    }
    import DivType._
    
    def rssCode(id:String, div:DivType.Value) :String = {
      val divStr = div match {
        case OUTSTANDING => "/AD%d".format(row)
        case CURRENT     => "/C%d".format(row)
        case _ => ""
      }
      "=RSS|'%s.%s'!%s%s".format(code, market, id, divStr)
    }
    
    Map("»l"     -> rssCode("»Ýl", NONE),
        "Å"     -> rssCode("ÅÇCzl", NONE),
        "Å"   -> rssCode("ÅÇCzÊ", NONE),
        "Å"     -> rssCode("ÅÇCzl", NONE),
        "Å"   -> rssCode("ÅÇCzÊ", NONE),
        "OI"     -> rssCode("OúIl", NONE),
        "Oä"     -> rssCode("Oúä¦", NONE),
        "o"     -> rssCode("o", OUTSTANDING),
        "ú"     -> rssCode("zú", NONE),
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
    
    ChartMaker.make(data("ID"), data("¼Ì"), data("ÁF"), data("Æ"))
    
    val order = List("ID", "¼Ì", "l", 
                     "Å", "Å", "Å", "Å",
                     "»l", "OI", "Oä", "o",
                     "c", "cT·", "c", "cT·",
                     "N", "Nú", "NÀ", "NÀú",
                     "", "v", "«", "ROE", "©",
                     "PER", "PBR",
                     "Z", "DÒ", "ú",
                     "­s", "¿", "s", "ªÞ",
                     "ã\", "Ý§", "ãê", "ú",
                     "]A", "]P", "î", "û")
    val list = order.map(data(_))
    list.mkString("\t")
  }
  
  val startLine = 2
  val codeList = makeCodeList()
  val codeRowPair = codeList zip Range(startLine, codeList.size+startLine)
  val strings = codeRowPair.map(makeString)
  val result = strings.mkString("\n")
  writeFile(result)
  println("OK!!")
}