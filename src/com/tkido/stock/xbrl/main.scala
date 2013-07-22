package com.tkido.stock.xbrl

object main extends App {
  import java.io.File
  //val codes = new File(Config.rootPath).listFiles.toList.filter(_.isDirectory).map(_.getName)
  val codes = List("3085")
  val companies = codes.map(Company(_))
  for(company <- companies) println(company)
}
