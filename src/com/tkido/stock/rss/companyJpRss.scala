package com.tkido.stock.rss

class CompanyJpRss(code:String) extends CompanyJp(code) {
  override val data = makeData
  
  override def makeData :Map[String, String] = {    
    val rssData = makeRssData
    super.makeData ++ rssData
  }
  
  def makeRssData :Map[String, String] = {
    object DivType extends Enumeration {
      val OUTSTANDING, CURRENT, NONE = Value
    }
    import DivType._
    
    def rssCode(id:String, div:DivType.Value) :String = {
      val divStr = div match {
        case OUTSTANDING => "/AD%d"
        case CURRENT     => "/C%d"
        case _ => ""
      }
      "=RSS|'%s.T'!%s%s".format(code, id, divStr)
    }
    
    Map("現値"     -> rssCode("現在値", NONE),
        "最売"     -> rssCode("最良売気配値", NONE),
        "最売数"   -> rssCode("最良売気配数量", NONE),
        "最買"     -> rssCode("最良買気配値", NONE),
        "最買数"   -> rssCode("最良買気配数量", NONE),
        "前終"     -> rssCode("前日終値", NONE),
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
}

object CompanyJpRss{
  def apply(code:String) = {
    new CompanyJpRss(code)
  }
}


