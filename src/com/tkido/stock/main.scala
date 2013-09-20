package com.tkido.stock

object main extends App {
  import com.tkido.stock.rss.ChartMaker
  import com.tkido.stock.rss.Company
  import com.tkido.stock.ufo.XbrlDownloader
  import com.tkido.tools.Text
  import com.tkido.tools.Logger
  
  Logger.level = Config.logLevel
  
  val codes = Text.readLines("data/table.txt")
  
  for(code <- codes) XbrlDownloader(code)
  
  val range = Range(Config.offset, Config.offset + codes.size)
  val companies = (codes zip range).par.map(p => Company(p._1, p._2))
  
  Text.write("data/result.txt", companies.mkString("\n"))
  
  for(company <- companies) ChartMaker(company)
  
  Logger.close()
}
