package com.tkido.stock.xbrl

object main extends App {
  println("start")
  val codes = List("5918")
  val companies = codes.map(Company(_))
  
  for(company <- companies) {
    println(company + ":解散価値:" + company.breakupValue())
    println(company + ":ネットキャッシュ:" + company.netCash())
    println(company + ":アクルーアル:" + company.accruals())
  }
}