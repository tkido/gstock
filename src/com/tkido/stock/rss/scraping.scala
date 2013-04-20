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
    val html = HtmlScraper(url)
    
    def getFeature() :String =
      getNextLineOf(html, """<th width="1%" nowrap>特色</th>""".r)
    def getConsolidated() :String =
      getNextLineOf(html, """<th nowrap>連結事業</th>""".r)
    def getCategory() :String =
      getNextLineOf(html, """<th nowrap>業種分類</th>""".r)
    def getRepresentative() :String = {
      val raw = getNextLineOf(html, """<th nowrap>代表者名</th>""".r)
      val name = raw.replaceAll("　", "")
      """=HYPERLINK("https://www.google.co.jp/search?q=%s", "%s")""".format(URLEncoder.encode(name, "UTF-8"), name)
    }
    def getFoundated() :String =
      getNextLineOf(html, """<th nowrap>設立年月日</th>""".r).slice(0, 4)
    def getListed() :String =
      getNextLineOf(html, """<th nowrap>上場年月日</th>""".r).slice(0, 4)
    def getSettlement() :String =
      getNextLineOf(html, """<th nowrap>決算</th>""".r).dropRight(2)
    def getSingleEmployees() :String =
      getNextLineOf(html, """<th width="1%">従業員数<br><span class="yjSt">（単独）</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      getNextLineOf(html, """<th width="1%">従業員数<br><span class="yjSt">（連結）</span></th>""".r).dropRight(1)
    def getAge() :String =
      getNextLineOf(html, """<th nowrap>平均年齢</th>""".r).dropRight(1)
    def getIncome() :String =
      getNextLineOf(html, """<th nowrap>平均年収</th>""".r).dropRight(3).replaceAll(",", "")
    
    Map("特色" -> getFeature,
        "連結事業" -> getConsolidated,
        "業務" -> getCategory,
        "設立" -> getFoundated,
        "上場" -> getListed,
        "決"   -> getSettlement,
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
      getNextLineOf(html, """<td bgcolor="#ebf4ff">決算発表日</td>""".r)
    def getCapitalToAssetRatio() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">自己資本比率</td>""".r)
    def getRoe() :String =
      getNextLineOf(html, """<td bgcolor="#ebf4ff">ROE（自己資本利益率）</td>""".r)
    
    Map("前決" -> getSettlement,
        "自" -> getCapitalToAssetRatio,
        "ROE" -> getRoe)
  }
  
  def parseStockholderPage(code:String) :Map[String, String] = {
    val url = "http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code)
    val html = HtmlScraper(url)
    
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
    //val list = parseProfilePage(code) ::: parseConsolidatePage(code) ::: parseStockholderPage(code) 
    val parsedData = parseDetailPage(code) ++
                     parseStockholderPage(code)
               
    //val data = parseProfilePage(code)
    //val data = parseConsolidatePage(code)
    //val data = parseStockholderPage(code)
    val pushedData = pushData(code, parsedData("市ID"), row)
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
        "名称"     -> rssCode("銘柄名称", ""),
        "現値"     -> rssCode("現在値", ""),
        "前比"     -> rssCode("前日比率", ""),
        "出来"     -> rssCode("出来高", "O"),
        "買残"     -> rssCode("信用買残", "O"),
        "買残週差" -> rssCode("信用買残前週比", "O"),
        "売残"     -> rssCode("信用売残", "O"),
        "売残週差" -> rssCode("信用売残前週比", "O"),
        "年高"     -> rssCode("年初来高値", "C"),
        "年高日"   -> rssCode("年初来高値日付", ""),
        "年安"     -> rssCode("年初来安値", "C"),
        "年安日"   -> rssCode("年初来安値日付", ""),
        "市"       -> rssCode("市場部略称", ""),
        "利"       -> rssCode("配当", "C"),
        "PER"      -> rssCode("ＰＥＲ", "C"),
        "PBR"      -> rssCode("ＰＢＲ", "C"))
  }
  
  def makeString(pair:Pair[String, Int]) :String = {
    val data = makeData(pair)
    val order = List("ID", "発行", "優待", "利", "出来")
    val list = order.map(data(_))
    list.mkString("\t")
  }
  
  val codeList = makeCodeList()
  val codeAndNum = codeList zip Range(2, codeList.size+2)
  val strings = codeAndNum.map(makeString)
  val result = strings.mkString("\n")
  println(result)
}