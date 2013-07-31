package com.tkido.stock.rss

object main extends App {
  import com.tkido.stock.xbrl
  Logger.level = Config.loglevel
  
  val codes = TextFile.readLines("data/rss/table.txt")
  val companies = codes.par.map(Company(_))
  
  val strings =
    for((company, index) <- companies.zipWithIndex)
      yield company.toStringForExcel(Config.offset + index)
  TextFile.writeString("data/rss/result.txt", strings.mkString("\n"))
  
  companies.collect{ case company:CompanyJp => ChartMaker.make(company)}
  
  Logger.close()
}
