package com.tkido.stock.page

object PageMaker {
  import com.tkido.stock.spider.Company
  import com.tkido.stock.spider.CompanyJp
  import com.tkido.stock.spider.CompanyUs
  
  def apply(company:Company){
    company match {
      case c:CompanyJp => PageMakerJp(c)
      case c:CompanyUs => PageMakerUs(c)
      case _           => ()
    }
  }
}