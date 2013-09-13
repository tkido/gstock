package com.tkido.stock.xbrl

object main extends App {
  import com.tkido.stock.Config
  import java.io.File
  
  //val codes = new File(Config.xbrlPath).listFiles.toList.filter(_.isDirectory).map(_.getName)
  val codes = List("5988")
  val companies = codes.map(Company(_))
  for(company <- companies) println(company)
}
