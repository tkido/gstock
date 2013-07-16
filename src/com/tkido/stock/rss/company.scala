package com.tkido.stock.rss

abstract class Company(code:String) {
  println("Company:%s".format(code))
  val data :Map[String, String]
  
  def toStringForExcel(row:Int) :String = {
    val list = Company.order.map(data(_).replaceAll("%d", row.toString))
    list.mkString("\t")
  }
}
object Company{
  val order = List("ID", "名称", "値", 
                   "最売", "最売数", "最買", "最買数",
                   "現値", "前終", "前比", "出来",
                   "買残", "買残週差", "売残", "売残週差",
                   "年高", "年高日", "年安", "年安日",
                   "利", "益", "性", "ROE", "自",
                   "PER", "PBR",
                   "決算", "優待", "落日",
                   "発行", "時価", "市", "分類",
                   "代表", "設立", "上場", "決期",
                   "従連", "従単", "齢", "収")  
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) :Company = {
    code match {
      case reJp() => CompanyJp(code)
      case reUs() => CompanyUs(code)
    }
  }
}
