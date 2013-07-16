package com.tkido.stock.rss

class Company(code:String) {
  println("Company:%s".format(code))
}
object Company{
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) :Company = {
    code match {
      case reJp() => CompanyJp(code)
      case reUs() => CompanyUs(code)
      case _      => new Company(code)
    }
  }
}
