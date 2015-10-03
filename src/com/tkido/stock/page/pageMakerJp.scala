package com.tkido.stock.page

import com.tkido.tools.Log
import com.tkido.tools.Tengine
import com.tkido.tools.Text
import java.util.Date

import scala.util.matching.Regex

object PageMakerJp {
  private val tEngine = Tengine("data/rss/templateJP.html")
  private val tData = Tengine("data/rss/templateData.html")
  private val tDiv = Tengine("data/rss/templateDiv.html")
  
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
    
    case class Sector(header:String, rows:String)
    def toSector(str:String) :Sector = {
      val (header, str2) = matchedAndRest("""【.*?】""", str)
      Log f header
      Log f str2

      val reData = """(.*?)(\d+)(\(-?\d+\))?""".r
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
      Sector(header, rows)
    }
    
    val sectors = collectMatched("""【.*?】[^【]*""", str1).map(toSector(_))
    Log f sectors
    
    val secnums = (sectors zip Range(0, sectors.size))
    
    val today = "%tY_%<tm%<td".format(new Date)
    
    val divs = secnums.map{case (sec, i) =>
      tDiv(data ++ Map("today" -> today, "num" -> i.toString, "header" -> sec.header))
    }.mkString("\n")
    
    val jss = secnums.map{case (sec, i) =>
      tData(data ++ Map("today" -> today, "num" -> i.toString, "rows" -> sec.rows))
    }.mkString("\n")
   
    
    val added = Map("date" -> date,
                     "divs" -> divs,
                     "jss" -> jss,
                     "today" -> today)
    
    val html = tEngine(data ++ added)
    Text.write("data/rss/%s.html".format(data("ID")), html)
  }
}