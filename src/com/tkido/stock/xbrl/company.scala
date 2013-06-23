package com.tkido.stock.xbrl

class Company(code:String) {
  import java.io.File
  import Math.pow
  
  val files = XbrlFinder.find(code)
  val reports = files.map(Report(_))
  
  def growthRate :Double = {
    if(reports.size == 1) return 1.0
    val size = reports.size-1
    val rate = reports.last.netIncome.toDouble / reports.head.netIncome.toDouble
    pow(rate, (1.0/size))
  }
  
  override def toString = {
    val buf = new StringBuilder
    buf ++= "ÉRÅ[Éh\t%s\n".format(code)
    for(report <- reports)
      buf ++= report.toString()
    buf ++= "ê¨í∑ó¶\t%s\n".format(growthRate)
    buf.toString
  }
}

object Company{
  def apply(code:String) = new Company(code)
}