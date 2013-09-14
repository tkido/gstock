package com.tkido.stock

object main extends App {
  import com.tkido.stock.rss.ChartMaker
  import com.tkido.stock.rss.Company
  import com.tkido.stock.rss.CompanyJp
  import com.tkido.stock.ufo.XbrlDownloader
  import com.tkido.tools.Text
  import com.tkido.tools.Logger
  
  Logger.level = Config.logLevel
  
  val codes = Text.readLines("data/table.txt")
  
  for(code <- codes) { XbrlDownloader.download(code) }
  
  val range = Range(Config.offset, Config.offset + codes.size)
  val pairs = codes zip range
  val companies = pairs.par.map(pair => Company(pair._1, pair._2))
  
  val strings = companies.map(_.toString)
  Text.write("data/result.txt", strings.mkString("\n"))
  
  companies.collect{ case company:CompanyJp => ChartMaker.make(company) }
  
  Logger.close()
}
