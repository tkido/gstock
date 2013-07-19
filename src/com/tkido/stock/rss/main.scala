package com.tkido.stock.rss

object main extends App {
  def main(){
    val codes = TextFile.readLines("data/rss/table.txt")
    val companies = codes.map(Company(_))
    for(company <- companies) ChartMaker.make(company)
    val strings =
      for((company, index) <- companies.zipWithIndex)
        yield company.toStringForExcel(Config.offset + index)
    TextFile.writeString("data/rss/result.txt", strings.mkString("\n"))
  }
  
  try{
    Logger.level = Logger.FATAL
    main()
  }finally{
    Logger.close()
  }
}
