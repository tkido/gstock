package com.tkido.stock.page

import com.tkido.tools.Log
import com.tkido.tools.Tengine
import com.tkido.tools.Text
import com.tkido.tools.thisOrElse
import com.tkido.tools.Regx.{matchedAndRest, collectMatched}
import java.util.Date

object PageMakerJp {
  private val tEngine = Tengine("data/rss/templateJP.html")
  private val tData = Tengine("data/rss/templateData.html")
  private val tDiv = Tengine("data/rss/templateDiv.html")

  def apply(data:Map[String, String]){
    val business = data.getOrElse("事業", "")
    
    val (date, str1) = matchedAndRest("""\(\d{4}\.\d{1,2}\)""", business)
    
    case class Sector(header:String, rows:String)
    def toSector(str:String) :Sector = {
      val (header, str2) = matchedAndRest("""【.*?】""", str)

      val reData = """(.*?)(\d+)(\(-?\d+\))?""".r
      val rows = {
        def stringToPairs(raw: String): Tuple2[String, String] =
          raw match{
            case reData(title, rate, plofit) =>
              (title + thisOrElse(plofit, "")) -> rate
          }
        str2
          .split('、').map(stringToPairs)
          .map(p => s"""['${p._1}', ${p._2}]""")
          .mkString(",\n")
      }
      Sector(header, rows)
    }
    
    val sectors = collectMatched("""【.*?】[^【]*""", str1).map(toSector(_))    
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