package com.tkido.stock.xbrl

object main extends App {
  println("start")
  val codes = List("5918")
  val companies = codes.map(Company(_))
  
  for(company <- companies) {
    println(company + ":���U���l:" + company.breakupValue())
    println(company + ":�l�b�g�L���b�V��:" + company.netCash())
    println(company + ":�A�N���[�A��:" + company.accruals())
  }
}