package com.tkido.stock.rss

class CompanyUs(code:String) extends Company(code) {
  println("CompanyUs:%s".format(code))
  val data = Map("" -> "")
}
object CompanyUs{
  def apply(code:String) = {
    new CompanyUs(code)
  }
}