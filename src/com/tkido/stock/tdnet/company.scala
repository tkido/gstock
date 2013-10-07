package com.tkido.stock.tdnet

class Company(code:String) {
  import com.tkido.tools.Html
  import scala.math.pow
  
  val files = XbrlFinder(code)
  val reports = files.map(Report(_))
}

object Company{
  def apply(code:String) = new Company(code)
}