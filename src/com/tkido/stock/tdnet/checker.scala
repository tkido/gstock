package com.tkido.stock.tdnet

object Checker{
  def apply(code:String) :Boolean = {
    val rawReports =
      XbrlFinder(code).map(XbrlParser(_))
        .groupBy(_.id).mapValues(_.last).toList.map(_._2) //distinct
        .sorted
    
    val rmap = rawReports.groupBy(_.id).mapValues(_.head)
    def toDeltaReport(report:Report[Long]) :Option[Report[Long]] = {
      if(report.lastQuarterId.isEmpty) return Some(report)  //Q1
      val lastId = report.lastQuarterId.get
      if(!rmap.contains(lastId)) return None
      val last = rmap(lastId)
      val delta = (report.data zip last.data).map(p => p._1 - p._2)
      Some(report.copy(data = delta))
    }
    val deltaReports = rawReports.map(toDeltaReport).collect{case Some(r) => r}
    
    val dmap = deltaReports.groupBy(_.id).mapValues(_.head)
    def toDoubleReport(report:Report[Long]) :Option[Report[Double]] = {
      val lastId = report.lastYearId
      if(!dmap.contains(lastId)) return None
      val last = dmap(lastId)
      def toDisplay(pair:Pair[Long, Long]) :Double = {
        pair match {
          case (p1, p2) if p1 >  0 && p2 >  0 => 1.0 * p1 / p2 - 1.0
          case _ => 0.0
        }
      }
      val ratio = (report.data zip last.data).map(toDisplay)
      Some(report.copy(data = ratio))
    }
    val doubleReports = deltaReports.map(toDoubleReport).collect{case Some(r) => r}
    val report = doubleReports.last
    
    report.data(0) > 0.10 && report.data(3) > 0.20
  }

}