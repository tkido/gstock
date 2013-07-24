package com.tkido.stock.rss

abstract class CompanyJp(code:String) extends Company(code) {
  import com.tkido.stock.xbrl
  
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
      html.getPreviousLineOf("""<dt class="title">­sÏ®""".r).dropRight(12)
    
    Map("­s" -> getOutstanding)
  }
  
  def parseProfilePage :Map[String, String] = {
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
  
  def parseConsolidatePage :Map[String, String] = {
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
  
  def makeOtherData :Map[String, String] = {
    def getEnterpriseValue() :String =
      try{
        xbrl.Company(code).fairValue.toString
      }catch{
        case _ => ""
      }
    Map("ID"   -> code,
        "l"   -> """=IF(y»lz=" ", yOIz, y»lz)""",
        "¿" -> """=ylz*y­sz/100000""",
        "v"   -> """=IF(yPERz=0, 0, 1/yPERz""",
        "«"   -> """=IF(yvz=0, 0, yz/yvz""",
        "¦"   -> """=IF(yé¿z=0, 0, ylz/y¿z)""",
        "¿" -> """=IF(yé¿z="", 0, yé¿z/1000/y­sz)""",
        "XV" -> Logger.today,
        "é¿" -> getEnterpriseValue )
  }  
  
  def parseStockholderPage :Map[String, String] = {
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
  
}
object CompanyJp{
  val reJpT = """Ø.*""".r
  
  def apply(code:String) :CompanyJp = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    html.getNextLineOf("""<dt>%s</dt>""".format(code).r) match {
      case reJpT()    => CompanyJpRss(code)
      case "}U[Y" => CompanyJpRss(code)
      case _          => CompanyJpOther(code)
    }
  }
}


