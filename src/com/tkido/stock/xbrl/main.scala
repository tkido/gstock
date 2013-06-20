package com.tkido.stock.xbrl

object main extends App {
  println("start")
  val codes = List("5918")
  val companies = codes.map(Company(_))
  
  for(company <- companies) println(company)
}