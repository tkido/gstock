package com.tkido.stock.rss

abstract class CompanyJp(code:String) extends Company(code) {
    
  def makeData :Map[String, String] = {    
    val parsedData = parseProfilePage ++
                     parseConsolidatePage ++
                     parseDetailPage ++
                     parseStockholderPage
    val otherData = makeOtherData
    parsedData ++ otherData
  }
  
  def parseDetailPage :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    
    def getOutstanding() :String =
      html.getPreviousLineOf("""<dt class="title">発行済株式数""".r).dropRight(12)
    
    Map("発行" -> getOutstanding)
  }
  
  def parseProfilePage :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code))
    
    def getName() :String = {
      val raw = html.getNextLineOf("""<meta http-equiv="Refresh" content="60">""".r)
      raw.dropRight(27).replaceFirst("""\(株\)""", "")
    }
    def getFeature() :String =
      html.getNextLineOf("""<th width="1%" nowrap>特色</th>""".r).replaceFirst(""" \[企業特色\]""", "")
    def getConsolidated() :String =
      html.getNextLineOf("""<th nowrap>連結事業</th>""".r)
    def getCategory() :String = {
      html.getNextLineOf("""<th nowrap>業種分類</th>""".r).replaceAll("業", "").replaceAll("・", "")
    }
    def getRepresentative() :String = {
      val raw = html.getNextLineOf("""<th nowrap>代表者名</th>""".r)
      raw.replaceAll("　", "").replaceAll(""" \[役員\]""", "")
    }
    def getFoundated() :String =
      html.getNextLineOf("""<th nowrap>設立年月日</th>""".r).slice(0, 4)
    def getListed() :String =
      html.getNextLineOf("""<th nowrap>上場年月日</th>""".r).slice(0, 4)
    def getSettlement() :String = {
      html.getNextLineOf("""<th nowrap>決算</th>""".r).replaceAll("末日", "").replaceAll(""" \[決算情報　年次\]""", "")
    }
    def getSingleEmployees() :String =
      html.getNextLineOf("""<th width="1%">従業員数<br><span class="yjSt">（単独）</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      html.getNextLineOf("""<th width="1%">従業員数<br><span class="yjSt">（連結）</span></th>""".r).dropRight(1)
    def getAge() :String =
      html.getNextLineOf("""<th nowrap>平均年齢</th>""".r).dropRight(1)
    def getIncome() :String =
      html.getNextLineOf("""<th nowrap>平均年収</th>""".r).dropRight(3).replaceAll(",", "")
    
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
  
  def parseConsolidatePage :Map[String, String] = {
    val html = Html("http://profile.yahoo.co.jp/consolidate/%s".format(code), "EUC-JP")
    
    def getSettlement() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">決算発表日</td>""".r).replaceFirst("---", "-")
    def getCapitalToAssetRatio() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">自己資本比率</td>""".r).replaceFirst("---", "-")
    def getRoe() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">ROE（自己資本利益率）</td>""".r).replaceFirst("---", "-")
    
    Map("決算" -> getSettlement,
        "自"   -> getCapitalToAssetRatio,
        "ROE"  -> getRoe)
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
        "値"   -> getPrice,
        "時価" -> getCap,
        "益"   -> getEpr,
        "性"   -> getPayoutRatio)
  }  
  
  def parseStockholderPage :Map[String, String] = {
    val html = Html("http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code))
    
    def getMonth() :String = {
      val rgex = """<tr><th>権利確定月</th><td>(.*?)</td></tr>""".r
      val opt = html.lines.collectFirst{ case rgex(m) => m }
      if(opt.isDefined)
        Html.removeTags(opt.get.replaceFirst("権利確定月", "").replaceAll("末日", ""))
      else
        ""
    }
    Map("優待" -> getMonth)
  }  
  
}
object CompanyJp{
  val reJpT = """東証.*""".r
  
  def apply(code:String) :CompanyJp = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    html.getNextLineOf("""<dt>%s</dt>""".format(code).r) match {
      case reJpT()    => CompanyJpRss(code)
      case "マザーズ" => CompanyJpRss(code)
      case _          => CompanyJpOther(code)
    }
  }
}


