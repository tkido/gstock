package com.tkido.stock.xbrl

object main extends App {
  import java.io.File
  val codes = new File("data/xbrl/").listFiles.toList.filter(_.isDirectory).map(_.getName)
  val companies = codes.map(Company(_))
  for(company <- companies) println(company)
}