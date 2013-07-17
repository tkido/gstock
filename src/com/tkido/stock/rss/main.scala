package com.tkido.stock.rss

object main extends App {
  val codes = TextFile.readLines("data/rss/table.txt")
  val companies = codes.map(Company(_))
  val strings =
    for((company, index) <- companies.zipWithIndex)
      yield company.toStringForExcel(index + Config.offset)
  TextFile.writeString("data/rss/result.txt", strings.mkString("\n"))
  println("OK!!")
}
