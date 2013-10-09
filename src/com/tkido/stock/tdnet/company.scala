package com.tkido.stock.tdnet

class Company(code:String) {
  import com.tkido.tools.Html
  import com.tkido.tools.Logger
  
  val files = XbrlFinder(code)
  val rawReports = files.map(XbrlParser(_)).sorted
  if(Logger.isDebug) for(r <- rawReports) Logger.log(r)
  
  val rmap = rawReports.groupBy(_.id).mapValues(_.last)
  def toDeltaReport(report:Report[Long]) :Option[Report[Long]] = {
    if(report.lastQuarterId.isEmpty) return Some(report)  //Q1
    val lastId = report.lastQuarterId.get
    if(!rmap.contains(lastId)) return None
    val last = rmap(lastId)
    val delta = (report.data zip last.data).map(p => p._1 - p._2)
    Some(report.copy(data = delta))
  }
  val deltaReports = rawReports.map(toDeltaReport).collect{case Some(r) => r}
  if(Logger.isDebug) for(r <- deltaReports) Logger.log(r)
  
  val dmap = deltaReports.groupBy(_.id).mapValues(_.last)
  def toDoubleReport(report:Report[Long]) :Option[Report[Double]] = {
    val lastId = report.lastYearId
    if(!dmap.contains(lastId)) return None
    val last = dmap(lastId)
    val ratio = (report.data zip last.data).map(p => 1.0 * p._1 / p._2 - 1.0)
    Some(report.copy(data = ratio))
  }
  val doubleReports = deltaReports.map(toDoubleReport).collect{case Some(r) => r}
  if(Logger.isDebug) for(r <- doubleReports) Logger.log(r)
  
  override def toString = {
    val header = Html.toTrTh("年度", "Q", "売上", "営利", "経利", "純利")
    Html.toTable(header :: doubleReports.reverse) +
    Html.toTable(header :: deltaReports.reverse)
  }
}

object Company{
  def apply(code:String) = new Company(code)
}