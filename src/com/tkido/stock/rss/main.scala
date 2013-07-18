package com.tkido.stock.rss

object main extends App {
  val codes = TextFile.readLines("data/rss/table.txt")
  val companies = codes.map(Company(_))
  for(company <- companies) ChartMaker.make(company)
  val strings =
    for((company, index) <- companies.zipWithIndex)
      yield company.toStringForExcel(Config.offset + index)
  TextFile.writeString("data/rss/result.txt", strings.mkString("\n"))
  println("OK!!")
}
