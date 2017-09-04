package com.tkido.stock.tdnet

object Checker{
  def apply(code:String) :Boolean = {
    val rawReports =
      XbrlFinder(code).map(XbrlParser(_))
        .groupBy(_.id).mapValues(_.last).toList.map(_._2) //distinct
        .sorted
    
    val rMap = rawReports.map(r => r.id -> r).toMap
    def toDeltaReport(report:Report[Long]) :Option[Report[Long]] = {
      report.lastQuarterId match{
        case None => Some(report) //Q1
        case Some(lastId) if(!rMap.contains(lastId)) => None
        case Some(lastId) =>
          val last = rMap(lastId)
          val delta = (report.data zip last.data).map(p => p._1 - p._2)
          Some(report.copy(data = delta))
      }
    }
    val deltaReports = rawReports.map(toDeltaReport).collect{case Some(r) => r}
    
    val dMap = deltaReports.map(r => r.id -> r).toMap
    def toDoubleReport(report:Report[Long]) :Option[Report[Double]] = {
      val lastId = report.lastYearId
      if(!dMap.contains(lastId)) return None
      val last = dMap(lastId)
      def toDisplay(pair:Tuple2[Long, Long]) :Double = {
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