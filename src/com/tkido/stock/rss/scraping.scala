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
      getPreviousLineOf(html, """<dt class="title">発行済株式数""".r).dropRight(12)
    def getMarketCode() :String = {
      getNextLineOf(html, """<dt>%s</dt>""".format(code).r) match {
        case "東証" => "T"
        case "東証1部" => "T"
        case "東証2部" => "T"
        case "マザーズ" => "T"
        case "大証1部" => "OS"
        case "大証2部" => "OS"
        case "JQG" => "Q"
        case "JQS" => "Q"
        case _ => "X"
      }
    }
    Map("市ID" -> getMarketCode,
        "発行" -> getOutstanding)
  }  
  
  def parseProfilePage(code:String) :Map[String, String] = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code)
    val html = Source.fromURL(url, "UTF-8").getLines.toIterable
    
    def getName() :String = {
      val raw = getNextLineOf(html, """<meta http-equiv="Refresh" content="60">""".r)
      val name = raw.dropRight(27).replaceFirst("""\(株\)""", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFeature() :String =
      getNextLineOf(html, """<th width="1%" nowrap>特色</th>""".r).replaceFirst(""" \[企業特色\]""", "")
    def getConsolidated() :String =
      getNextLineOf(html, """<th nowrap>連結事業</th>""".r)
    def getCategory() :String = {
      val name = getNextLineOf(html, """<th nowrap>業種分類</th>""".r).replaceAll("業", "").replaceAll("・", "")
      """=HYPERLINK("http://kabu-sokuhou.com/brand/item/code___%s/", "%s")""".format(code, name)
    }
    def getRepresentative() :String = {
      val raw = getNextLineOf(html, """<th nowrap>代表者名</th>""".r)
      val name = raw.replaceAll("　", "").replaceAll(""" \[役員\]""", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFoundated() :String =
      getNextLineOf(html, """<th nowrap>設立年月日</th>""".r).slice(0, 4)
    def getListed() :String =
      getNextLineOf(html, """<th nowrap>上場年月日</th>""".r).slice(0, 4)
    def getSettlement() :String = {
      val name = getNextLineOf(html, """<th nowrap>決算</th>""".r).replaceAll("末日", "").replaceAll(""" \[決算情報　年次\]""", "")
      """=HYPERLINK("http://www.nikkei.com/markets/company/kigyo/kigyo.aspx?scode=%s", "%s")""".format(code, name)
    }
    def getSingleEmployees() :String =
      getNextLineOf(html, """<th width="1%">従業員数<br><span class="yjSt">（単独）</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      getNextLineOf(html, """<th width="1%">従業員数<br><span class="yjSt">（連結）</span></th>""".r).dropRight(1)
    def getAge() :String =
      getNextLineOf(html, """<th nowrap>平均年齢</th>""".r).dropRight(1)
    def getIncome() :String =
      getNextLineOf(html, """<th nowrap>平均年収</th>""".r).dropRight(3).replaceAll(",", "")
    
    Map("名称" -> getName,
        "特色" -> getFeature,
        "事業" -> getConsolidated,
        "分類" -> getCategory,
        "設立" -> getFoundated,
        "上場" -> getListed,
        "決期" -> getSettlement,
        "従連" -> getConsolidatedEmployees,
        "従単" -> getSingleEmployees,
        "齢"   -> getAge,
        "収"   -> getIncome,
        "代表" -> getRepresentative)
  }
  
  def parseConsolidatePage(code:String) :Map[String, String] = {
    val url = "http://profile.yahoo.co.jp/consolidate/%s".format(code)
    val html = Source.fromURL(url, "EUC-JP").getLines.toIterable
    
    def getSettlement() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">決算発表日</td>""".r).replaceFirst("---", "-")
    def getCapitalToAssetRatio() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">自己資本比率</td>""".r).replaceFirst("---", "-")
    def getRoe() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">ROE（自己資本利益率）</td>""".r).replaceFirst("---", "-")
    
    Map("決算" -> getSettlement,
        "自"   -> getCapitalToAssetRatio,
        "ROE"  -> getRoe)
  }
  
  def parseStockholderPage(code:String) :Map[String, String] = {
    val url = "http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code)
    val html = Source.fromURL(url, "UTF-8").getLines.toIterable
    
    def getMonth() :String = {
      val rgex = """<tr><th>権利確定月</th><td>(.*?)</td></tr>""".r
      val opt = html.collectFirst{ case rgex(m) => m }
      if(opt.isDefined)
        """=HYPERLINK("%s", "%s")""".format(url, removeTags(opt.get.replaceFirst("権利確定月", "").replaceAll("末日", "")))
      else
        ""
    }
    Map("優待" -> getMonth)
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
    val rssData = makeRssData(code, parsedData("市ID"), row)
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
    
    Map("現値"     -> rssCode("現在値", NONE),
        "最売"     -> rssCode("最良売気配値", NONE),
        "最売数"   -> rssCode("最良売気配数量", NONE),
        "最買"     -> rssCode("最良買気配値", NONE),
        "最買数"   -> rssCode("最良買気配数量", NONE),
        "前比"     -> rssCode("前日比率", NONE),
        "出来"     -> rssCode("出来高", OUTSTANDING),
        "落日"     -> rssCode("配当落日", NONE),
        "買残"     -> rssCode("信用買残", OUTSTANDING),
        "買残週差" -> rssCode("信用買残前週比", OUTSTANDING),
        "売残"     -> rssCode("信用売残", OUTSTANDING),
        "売残週差" -> rssCode("信用売残前週比", OUTSTANDING),
        "年高"     -> rssCode("年初来高値", CURRENT),
        "年高日"   -> rssCode("年初来高値日付", NONE),
        "年安"     -> rssCode("年初来安値", CURRENT),
        "年安日"   -> rssCode("年初来安値日付", NONE),
        "市"       -> rssCode("市場部略称", NONE),
        "利"       -> rssCode("配当", CURRENT),
        "PER"      -> rssCode("ＰＥＲ", NONE),
        "PBR"      -> rssCode("ＰＢＲ", NONE))
  }
  
  def makeString(pair:Pair[String, Int]) :String = {
    val data = makeData(pair)
    val order = List("ID", "名称", "現値", 
                     "最売", "最売数", "最買", "最買数",
                     "前比", "出来",
                     "買残", "買残週差", "売残", "売残週差",
                     "年高", "年高日", "年安", "年安日",
                     "利", "PER", "PBR", "ROE", 
                     "自", "決算", "優待", "落日",
                     "発行", "市", "分類", "代表",
                     "特色", "事業", "設立", "上場", "決期",
                     "従連", "従単", "齢", "収")
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