package com.tkido.stock.rss

class CompanyJpRss(code:String) extends CompanyJp(code) {
  println("CompanyJpRss:%s".format(code))  
}
object CompanyJpRss{
  def apply(code:String) = {
    new CompanyJpRss(code)
  }
}


