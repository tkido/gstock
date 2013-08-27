package com.tkido.stock.xbrl

class Company(code:String) {
  import Math.pow
  
  val files = XbrlFinder.find(code)
  val reports = files.map(Report(_)).sort(_.year < _.year)
  
  def slope(list:List[BigInt]) :BigInt = {
    val range = Range(0, list.size)
    val m1 = range.map(x => x * list(x)).sum
    val m2 = range.sum * list(0)
    val m3 = range.map(x => x * x).sum
    (m1 - m2) / m3
  }
  
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
    //List(reports.last.freeCashFlow, reports.last.netIncome).max
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
  def fairValue() :BigInt =
    stock + flow * rate
  
  override def toString = {
    val header = "\t\t\t\t\t\t%s\t%s\t%s".format(code, growthRate, fairValue)
    val list = header :: reports.map(_.toString)
    list.mkString("\n")
  }
}

object Company{
  def apply(code:String) = new Company(code)
}