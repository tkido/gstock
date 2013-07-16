package com.tkido.stock.rss

class CompanyJp(code:String) extends Company(code) {
  println("CompanyJp:%s".format(code))
}
object CompanyJp{
  val reJpT = """東証.*""".r
  
  def apply(code:String) :CompanyJp = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    val rssFlag = {
      html.getNextLineOf("""<dt>%s</dt>""".format(code).r) match {
        case reJpT()    => true
        case "マザーズ" => true
        case _          => false
      }
    }
    if(rssFlag)
      CompanyJpRss(code)
    else
      new CompanyJp(code)
  }
}


