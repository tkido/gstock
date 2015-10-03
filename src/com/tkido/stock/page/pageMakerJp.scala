package com.tkido.stock.page

import com.tkido.tools.Tengine
import com.tkido.tools.Text
import java.util.Date

object PageMakerJp {
  private val tEngine = Tengine("data/rss/templateJP.html")
  
  def apply(data:Map[String, String]){
    val reDate = """\(\d{4}\.\d{1,2}\)"""
    val reHeader = "【.*?】"
    val reOther = "【.*"
    val reData = """(.*?)(\d+)(\(\d+\))?""".r
    
    val business = data.getOrElse("事業", "")
    
    val date =
      reDate.r.findFirstMatchIn(business) match {
        case Some(m) => m.group(0)
        case None    => ""
      }
    val header = 
      reHeader.r.findFirstMatchIn(business) match {
        case Some(m) => m.group(0)
        case None    => ""
      }
    val other = 
      reOther.r.findFirstMatchIn(business.replaceFirst(reHeader, "")) match {
        case Some(m) => m.group(0).replaceFirst(reDate, "")
        case None    => ""
      }
    val rows = {
      def stringToPairs(raw: String): Tuple2[String, String] =
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
    val today = "%tY_%<tm%<td".format(new Date)
    
    val added = Map("date" -> date,
                     "header" -> header,
                     "other" -> other,
                     "rows" -> rows,
                     "today" -> today)
    
    val html = tEngine(data ++ added)
    Text.write("data/rss/%s.html".format(data("ID")), html)
  }
}