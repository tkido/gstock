package com.tkido.stock.page

import com.tkido.tools.Log
import com.tkido.tools.Tengine
import com.tkido.tools.Text
import java.util.Date

import scala.util.matching.Regex

object PageMakerJp {
  private val tEngine = Tengine("data/rss/templateJP.html")
  
  def matchedAndRest(regp:String, str:String) :(String, String) = {
    val regx = regp.mkString("^.*?(", "", ").*$").r
    str match{
      case regx(s) => (s, str.replaceFirst(Regex.quote(s), ""))
      case _ => ("", str)
    }
  }
  
  def collectMatched(regp:String, str:String) :List[String] = {
    def go(regp:String, str:String, list:List[String]) :List[String] = {
      val regx = regp.mkString("^.*?(", "", ").*$").r
      str match{
        case regx(s) => s :: go(regp, str.replaceFirst(Regex.quote(s), ""), list)
        case _ => list
      }
    }
    go(regp, str, List())
  }
  
  def apply(data:Map[String, String]){
    Log f "PageMakerJp"
    val business = data.getOrElse("事業", "")
    Log f business
    
    val (date, str1) = matchedAndRest("""\(\d{4}\.\d{1,2}\)""", business)
    Log f date
    Log f str1
    val sectors = collectMatched("""【.*?】[^【]*""", str1)
    Log f sectors
    
    
    val reData = """(.*?)(\d+)(\(\d+\))?""".r
    for(sector <- sectors){
      val (header, str2) = matchedAndRest("""【.*?】""", sector)
      Log f header
      Log f str2

      val rows = {
        def stringToPairs(raw: String): Tuple2[String, String] =
          raw match{
            case reData(title, rate, plofit) =>
              if(plofit == null)
                title -> rate
              else
                (title + plofit) -> rate
          }

        str2
          .split('、').map(stringToPairs)
          .map(p => s"""['${p._1}', ${p._2}]""")
          .mkString(",\n")
      }
      Log f rows
    }
    
    
    
    
    val reBusiness = """(【.*?】)(.*?)(\(\d{4}\.\d{1,2}\))""".r
    
    val reDate = """\(\d{4}\.\d{1,2}\)"""
    val reHeader = "【.*?】"
    val reOther = "【.*"

    
    val reRestDate = """(.*?)(\(\d{4}\.\d{1,2}\))""".r
    val (rest1, date2) = business match{
      case reRestDate(rest, date) => (rest, date)
    }
    val reHeaderRest = """(【.*?】)(.*)""".r
    val (header2, rest2) = rest1 match{
      case reHeaderRest(header, rest) => (header, rest)
    }

    /*
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
    * 
    */
  }
}