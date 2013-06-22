package com.tkido.stock.xbrl

class Company(code:String) {
  import java.io.File
  val files = XbrlFinder.find(code)
  val reports = files.map(Report(_))
  
  override def toString = {
    val buf = new StringBuilder
    buf ++= "コード：%s\n".format(code)
    for(report <- reports)
      buf ++= report.toString()
    buf.toString
  }
}

object Company{
  def apply(code:String) = new Company(code)
}