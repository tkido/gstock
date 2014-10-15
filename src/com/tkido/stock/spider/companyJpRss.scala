package com.tkido.stock.spider

class CompanyJpRss(code:String, row:Int) extends CompanyJp(code, row) {
  override val data = makeData
  
  override def makeData :Map[String, String] =    
    super.makeData ++ makeRssData
  
  def makeRssData :Map[String, String] = {
    def rssCode(id:String, div:String = "") :String = {
      val divStr = if(div == "") "" else "/%s".format(div)
      "=RSS|'%s.T'!%s%s".format(code, id, divStr)
    }
    
    Map("現値"     -> rssCode("現在値"),
        "最売"     -> rssCode("最良売気配値"),
        "最売数"   -> rssCode("最良売気配数量"),
        "最買"     -> rssCode("最良買気配値"),
        "最買数"   -> rssCode("最良買気配数量"),
        "前終"     -> rssCode("前日終値"),
        "前比"     -> rssCode("前日比率"),
        "出来"     -> rssCode("出来高", "【発行】"),
        "落日"     -> rssCode("配当落日"),
        "買残"     -> rssCode("信用買残", "【発行】"),
        "買残週差" -> rssCode("信用買残前週比", "【発行】"),
        "売残"     -> rssCode("信用売残", "【発行】"),
        "売残週差" -> rssCode("信用売残前週比", "【発行】"),
        "年高"     -> rssCode("年初来高値", "【値】"),
        "年高日"   -> rssCode("年初来高値日付"),
        "年安"     -> rssCode("年初来安値", "【値】"),
        "年安日"   -> rssCode("年初来安値日付"),
        "市"       -> rssCode("市場部略称"),
        "利"       -> rssCode("配当", "【値】"),
        "PER"      -> rssCode("ＰＥＲ"),
        "PBR"      -> rssCode("ＰＢＲ"),
        "R"        -> "R" )
  }
}

object CompanyJpRss{
  def apply(code:String, row:Int) = {
    new CompanyJpRss(code, row)
  }
}


