package com.tkido.stock.page

object PageMakerJp {
  import com.tkido.stock.spider.Company
  import com.tkido.tools.Text
  import java.util.Date
  
  private val templete = Text.read("data/rss/templateJP.html")
  
  def apply(data:Map[String, String]){
    val code     = data.getOrElse("ID", "")
    val business = data.getOrElse("事業", "")
    val name     = data.getOrElse("名称", "")
    val feature  = data.getOrElse("特色", "")
    val edinet   = data.getOrElse("edinet", "")
    val tdnet    = data.getOrElse("tdnet", "")
    val log      = data.getOrElse("log", "")
    
    val reDate = """\(\d{4}\.\d{1,2}\)"""
    val reHeader = "【.*?】"
    val reOther = "【.*"
    val reData = """(.*?)(\d+)(\(\d+\))?""".r
    
    def getDate() :String =
      reDate.r.findFirstMatchIn(business) match {
        case Some(m) => m.group(0)
        case None    => ""
      }
    def getHeader() :String =
      reHeader.r.findFirstMatchIn(business) match {
        case Some(m) => m.group(0)
        case None    => ""
      }
    def getOther() :String =
      reOther.r.findFirstMatchIn(business.replaceFirst(reHeader, "")) match {
        case Some(m) => m.group(0).replaceFirst(reDate, "")
        case None    => ""
      }
    
    def getRows() :String = {
      def stringToPairs(raw: String): Pair[String, String] =
        reData.findFirstMatchIn(raw) match {
          case Some(m) if m.group(3) == null =>
            m.group(1) -> m.group(2)
          case Some(m) =>
            m.group(1)+m.group(3) -> m.group(2)
          case None =>
            "" -> ""
        }
      business.replaceFirst(reDate, "").replaceFirst(reHeader, "").replaceFirst(reOther, "")
        .split('、').map(stringToPairs)
        .map(p => """['%s', %s]""".format(p._1, p._2) ).mkString(",\n")
    }
    
    val title = code + " " + name + getHeader + getDate
    
    val today = "%tY_%<tm%<td".format(new Date)
    val tag = "%s_%s".format(today, code)
    
    val html = templete.format(title, title, feature, getOther, tag, edinet, tdnet, log, getRows, tag)
    Text.write("data/rss/%s.html".format(code), html)
  }
}