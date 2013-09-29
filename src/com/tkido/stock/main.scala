package com.tkido.stock

object main extends App {
  import com.tkido.stock.rss.ChartMaker
  import com.tkido.stock.rss.Company
  import com.tkido.stock.ufo.XbrlDownloader
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  
  Logger.level = Config.logLevel
  
  val codes = Text.readLines("data/table.txt")
  
  codes.map(XbrlDownloader(_))
  
  val range = Range(Config.offset, Config.offset + codes.size)
  val pairs = codes zip range
  
  val companies =
    if(Config.parFlag)
      pairs.par.map(p => Company(p._1, p._2))
    else
      pairs.map(p => Company(p._1, p._2))
  
  Text.write("data/result.txt", companies.mkString("\n"))
  
  companies.map(ChartMaker(_))
  
  Logger.close()
}
