package com.tkido.stock.tdnet

class Company(code:String) {
  import com.tkido.tools.Html
  import com.tkido.tools.Log
  
  val rawReports =
    XbrlFinder(code).map(XbrlParser(_))
      .groupBy(_.id).mapValues(_.last).toList.map(_._2) //distinct
      .sorted
  if(Log.isDebug) for(r <- rawReports) Log.log(r)
  
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
  if(Log.isDebug) for(r <- deltaReports) Log.log(r)
  
  val dmap = deltaReports.groupBy(_.id).mapValues(_.head)
  def toDoubleReport(report:Report[Long]) :Option[Report[Any]] = {
    val lastId = report.lastYearId
    if(!dmap.contains(lastId)) return None
    val last = dmap(lastId)
    def toDisplay(pair:Tuple2[Long, Long]) :Any = {
      pair match {
        case (p1, p2) if p1 >  0 && p2 >  0 => 1.0 * p1 / p2 - 1.0
        case (p1, p2) if p1 <= 0 && p2 <= 0 => """<span class="minus">赤字</span>"""
        case (p1, p2) if p1 >  0 && p2 <= 0 => "黒転"
        case (p1, p2) if p1 <= 0 && p2 >  0 => """<span class="minus">赤転</span>"""
      }
    }
    val ratio = (report.data zip last.data).map(toDisplay)
    Some(report.copy(data = ratio))
  }
  val doubleReports = deltaReports.map(toDoubleReport).collect{case Some(r) => r}
  if(Log.isDebug) for(r <- doubleReports) Log.log(r)
  
  override def toString = {
    val header = Html.toTrTh("終了月", "Q", "売上", "営利", "経利", "純利", "開示日")
    """<h3>TDnet 四半期報告書（前年同期比）</h3>""" +
    Html.toTable(header :: doubleReports.reverse) +
    """<h3>TDnet 四半期報告書（実数）</h3>""" +
    Html.toTable(header :: deltaReports.reverse)
  }
}

object Company{
  def apply(code:String) = new Company(code)
}