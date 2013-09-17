package com.tkido.stock.xbrl

class Company(code:String) {
  import com.tkido.tools.Html
  import scala.math.pow
  
  val files = XbrlFinder.find(code)
  val reports = files.map(Report(_))
  
  def growthRate :Double = {
    if(reports.size == 1) return 1.0
    if(reports.head.netIncome < 0) return 1.0
    val size = reports.size-1
    val rate = reports.last.netIncome.toDouble / reports.head.netIncome.toDouble
    pow(rate, (1.0 / size))
  }
  
  def stock :BigInt =
    List(reports.last.breakupValue, reports.last.netCash).min
  def flow :BigInt =
    reports.last.netIncome
  
  def rate :Int = {
    if(flow < 0) return 15
    val forecastedGrowthRate = if(growthRate < 1.0) growthRate else growthRate / 2 + 0.5
    def presentValue(later:Int) :Double =
      pow(forecastedGrowthRate, later)/pow(1.1, later)
    val doubleRate = Range(1, 5).map(presentValue).sum + presentValue(5) * 10
    val intRate = doubleRate.toInt
    List(intRate, 15).min
  }
  def fairValue :BigInt =
    stock + flow * rate
  
  override def toString = {
    val header = Html.toTrTh("年度", "解価", "NetC", "アク", "純利", "FCF", "粗率", "営率", "経率", "純率")
    Html.toTable(header :: reports.reverse)
  }
}

object Company{
  def apply(code:String) = new Company(code)
}