package com.tkido.stock.tdnet

class Company(code:String) {
  import com.tkido.tools.Html
  
  val files = XbrlFinder(code)
  val rawReports = files.map(XbrlParser(_)).sorted
  for(r <- rawReports) println(r)
  
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
  for(r <- deltaReports) println(r)
  
  val dmap = deltaReports.groupBy(_.id).mapValues(_.last)
  def toDoubleReport(report:Report[Long]) :Option[Report[Double]] = {
    val lastId = report.lastYearId
    if(!dmap.contains(lastId)) return None
    val last = dmap(lastId)
    val ratio = (report.data zip last.data).map(p => 1.0 * p._1 / p._2 - 1.0)
    Some(report.copy(data = ratio))
  }
  val doubleReports = deltaReports.map(toDoubleReport).collect{case Some(r) => r}
  for(r <- doubleReports) println(r)
  
}

object Company{
  def apply(code:String) = new Company(code)
}