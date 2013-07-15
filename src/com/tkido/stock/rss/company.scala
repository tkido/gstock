package com.tkido.stock.rss

class Company(code:String) {
  
}
object Company{
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) = {
    code match {
      case reJp() => println("Jp")
      case reUs() => println("Us")
      case _      => false
    }
    new Company(code)
  }
  
  
}

class CompanyJp(code:String) extends Company(code) {
  
}
