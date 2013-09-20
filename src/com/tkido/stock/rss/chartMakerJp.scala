package com.tkido.stock.rss

object ChartMakerJp {
  import com.tkido.tools.Text
  
  private val templete = Text.read("data/rss/template.html")
  
  def apply(company:Company){
    val data = company.data
    
    val code     = data.getOrElse("ID", "")
    val business = data.getOrElse("事業", "")
    val name     = data.getOrElse("名称", "")
    val feature  = data.getOrElse("特色", "")
    val table    = data.getOrElse("表", "")
    
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
          case None    => "" -> ""
          case Some(m) => {
            val g3 = if(m.group(3) == null) "" else m.group(3)
            Pair(m.group(1)+g3, m.group(2))
          }
        }
      val str = business.replaceFirst(reDate, "").replaceFirst(reHeader, "").replaceFirst(reOther, "")
      val pairs = str.split('、').map(stringToPairs)
      pairs.map(p => """['%s', %s]""".format(p._1, p._2) ).mkString(",\n")
    }
    
    val title = code + " " + name + getHeader + getDate
    
    val html = templete.format(title, getRows, title, feature, getOther, table)
    Text.write("data/rss/%s.html".format(code), html)
  }
}