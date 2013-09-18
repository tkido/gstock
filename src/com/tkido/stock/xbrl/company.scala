package com.tkido.stock.xbrl

class Company(code:String) {
  import com.tkido.tools.Html
  import scala.math.pow
  
  val files = XbrlFinder.find(code)
  val reports = files.map(Report(_))
  
  def getGrowthRate :Double = {
    if(reports.size == 1) return 1.0
    if(reports.head.netIncome < 0) return 1.0
    val size = reports.size-1
    val rate = reports.last.netIncome.toDouble / reports.head.netIncome.toDouble
    pow(rate, (1.0 / size))
  }
  val gr = getGrowthRate
  
  def getRate :Int = {
    if(flow < 0) return 15
    val fgr = if(gr < 1.0) gr else gr / 2 + 0.5 //forcastedGrowthRate
    def pv(later:Int) :Double =
      pow(fgr, later)/pow(1.1, later)
    val rate = Range(1, 5).map(pv).sum + pv(5) * 10
    List(rate.toInt, 15).min
  }
  
  def stock :BigInt =
    List(reports.last.breakupValue, reports.last.netCash).min
  def flow :BigInt =
    reports.last.netIncome
  def fairValue :BigInt =
    stock + flow * getRate
  
  override def toString = {
    val header = Html.toTrTh("年度", "解価", "NetC", "アク", "純利", "FCF", "粗率", "営率", "経率", "純率")
    Html.toTable(header :: reports.reverse)
  }
}

object Company{
  def apply(code:String) = new Company(code)
}