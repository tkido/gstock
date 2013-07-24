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
      html.getPreviousLineOf("""<dt class="title">”­sÏŠ”®”""".r).dropRight(12)
    
    Map("”­s" -> getOutstanding)
  }
  
  def parseProfilePage :Map[String, String] = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code))
    
    def getName() :String = {
      val raw = html.getNextLineOf("""<meta http-equiv="Refresh" content="60">""".r)
      raw.dropRight(27).replaceFirst("""\(Š”\)""", "")
    }
    def getFeature() :String =
      html.getNextLineOf("""<th width="1%" nowrap>“ÁF</th>""".r).replaceFirst(""" \[Šé‹Æ“ÁF\]""", "")
    def getConsolidated() :String =
      html.getNextLineOf("""<th nowrap>˜AŒ‹–‹Æ</th>""".r)
    def getCategory() :String = {
      html.getNextLineOf("""<th nowrap>‹Æí•ª—Ş</th>""".r).replaceAll("‹Æ", "").replaceAll("E", "")
    }
    def getRepresentative() :String = {
      val raw = html.getNextLineOf("""<th nowrap>‘ã•\Ò–¼</th>""".r)
      raw.replaceAll("@", "").replaceAll(""" \[–ğˆõ\]""", "")
    }
    def getFoundated() :String =
      html.getNextLineOf("""<th nowrap>İ—§”NŒ“ú</th>""".r).slice(0, 4)
    def getListed() :String =
      html.getNextLineOf("""<th nowrap>ãê”NŒ“ú</th>""".r).slice(0, 4)
    def getSettlement() :String = {
      html.getNextLineOf("""<th nowrap>ŒˆZ</th>""".r).replaceAll("––“ú", "").replaceAll(""" \[ŒˆZî•ñ@”NŸ\]""", "")
    }
    def getSingleEmployees() :String =
      html.getNextLineOf("""<th width="1%">]‹Æˆõ”<br><span class="yjSt">i’P“Æj</span></th>""".r).dropRight(1)
    def getConsolidatedEmployees() :String =
      html.getNextLineOf("""<th width="1%">]‹Æˆõ”<br><span class="yjSt">i˜AŒ‹j</span></th>""".r).dropRight(1)
    def getAge() :String =
      html.getNextLineOf("""<th nowrap>•½‹Ï”N—î</th>""".r).dropRight(1)
    def getIncome() :String =
      html.getNextLineOf("""<th nowrap>•½‹Ï”Nû</th>""".r).dropRight(3).replaceAll(",", "")
    
    Map("–¼Ì" -> getName,
        "“ÁF" -> getFeature,
        "–‹Æ" -> getConsolidated,
        "•ª—Ş" -> getCategory,
        "İ—§" -> getFoundated,
        "ãê" -> getListed,
        "ŒˆŠú" -> getSettlement,
        "]˜A" -> getConsolidatedEmployees,
        "]’P" -> getSingleEmployees,
        "—î"   -> getAge,
        "û"   -> getIncome,
        "‘ã•\" -> getRepresentative)
  }
  
  def parseConsolidatePage :Map[String, String] = {
    val html = Html("http://profile.yahoo.co.jp/consolidate/%s".format(code), "EUC-JP")
    
    def getSettlement() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">ŒˆZ”­•\“ú</td>""".r).replaceFirst("---", "-")
    def getCapitalToAssetRatio() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">©ŒÈ‘–{”ä—¦</td>""".r).replaceFirst("---", "-")
    def getRoe() :String =
      html.getNextLineOf("""<td bgcolor="#ebf4ff">ROEi©ŒÈ‘–{—˜‰v—¦j</td>""".r).replaceFirst("---", "-")
    
    Map("ŒˆZ" -> getSettlement,
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
        "’l"   -> """=IF(yŒ»’lz=" ", y‘OIz, yŒ»’lz)""",
        "‰¿" -> """=y’lz*y”­sz/100000""",
        "‰v"   -> """=IF(yPERz=0, 0, 1/yPERz""",
        "«"   -> """=IF(y‰vz=0, 0, y—˜z/y‰vz""",
        "—¦"   -> """=IF(yŠé‰¿z=0, 0, y’lz/yŠ”‰¿z)""",
        "Š”‰¿" -> """=IF(yŠé‰¿z="", 0, yŠé‰¿z/1000/y”­sz)""",
        "XV" -> Logger.today,
        "Šé‰¿" -> getEnterpriseValue )
  }  
  
  def parseStockholderPage :Map[String, String] = {
    val html = Html("http://info.finance.yahoo.co.jp/stockholder/detail/?code=%s".format(code))
    
    def getMonth() :String = {
      val rgex = """<tr><th>Œ —˜Šm’èŒ</th><td>(.*?)</td></tr>""".r
      val opt = html.lines.collectFirst{ case rgex(m) => m }
      if(opt.isDefined)
        Html.removeTags(opt.get.replaceFirst("Œ —˜Šm’èŒ", "").replaceAll("––“ú", ""))
      else
        ""
    }
    Map("—D‘Ò" -> getMonth)
  }  
  
}
object CompanyJp{
  val reJpT = """“ŒØ.*""".r
  
  def apply(code:String) :CompanyJp = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    html.getNextLineOf("""<dt>%s</dt>""".format(code).r) match {
      case reJpT()    => CompanyJpRss(code)
      case "ƒ}ƒU[ƒY" => CompanyJpRss(code)
      case _          => CompanyJpOther(code)
    }
  }
}


