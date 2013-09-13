package com.tkido.stock.rss

object main extends App {
  import com.tkido.stock.Config
  import com.tkido.stock.ufo.XbrlDownloader
  import com.tkido.tools.Text
  
  Logger.level = Config.logLevel
  
  val codes = Text.readLines("data/rss/table.txt")
  
  for(code <- codes) { XbrlDownloader.download(code) }
  
  val range = Range(Config.offset, Config.offset + codes.size)
  val pairs = codes zip range
  val companies = pairs.par.map(pair => Company(pair._1, pair._2))
  
  val strings = companies.map(_.toStringForExcel)
  Text.write("data/rss/result.txt", strings.mkString("\n"))
  
  companies.collect{
    case company:CompanyJp => ChartMaker.make(company)
  }
  
  Logger.close()
}
