package com.tkido.stock.rss

class Company(code:String) {
  println("Company:%s".format(code))
}
class CompanyUs(code:String) extends Company(code) {
  println("CompanyUs:%s".format(code))
}
class CompanyJp(code:String) extends Company(code) {
  println("CompanyJp:%s".format(code))
}
class CompanyJpRss(code:String) extends CompanyJp(code) {
  println("CompanyJpRss:%s".format(code))  
}


object Company{
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) :Company = {
    code match {
      case reJp() => CompanyJp(code)
      case reUs() => new CompanyUs(code)
      case _      => new Company(code)
    }
  }
}

object CompanyJp{
  def apply(code:String) :CompanyJp = {
    val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
    val rssFlag = {
      html.getNextLineOf("""<dt>%s</dt>""".format(code).r) match {
        case "東証"     => true
        case "東証1部"  => true
        case "東証2部"  => true
        case "マザーズ" => true
        case "大証1部"  => true
        case "大証2部"  => true
        case "東証JQG"  => true
        case "東証JQS"  => true
        case _          => false
      }
    }
    if(rssFlag)
      new CompanyJpRss(code)
    else
      new CompanyJp(code)
  }
}

