package com.tkido.stock.rss

object ChartMaker {
  def apply(company:Company){
    company match {
      case c:CompanyJp => ChartMakerJp(c)
      case _           => ()
    }
  }
}