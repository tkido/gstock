package com.tkido.stock.edinet

import com.tkido.tools.Html
import scala.math.pow

class Company(code:String) {
  val files = XbrlFinder(code)
  val reports = files.map(Report(_))
  
  def getGrowthRate :Double = {
    val repos = reports.takeRight(5) //use latest reports only
    
    if(repos.size <= 1) return 1.0
    if(repos.head.netIncome < 0) return 1.0
    val size = repos.size-1
    val rate = repos.last.netIncome.toDouble / repos.head.netIncome.toDouble
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
  
  def stock :Long =
    List(reports.last.breakupValue, reports.last.netCash).min
  def flow :Long =
    reports.last.netIncome
  def fairValue :Long = {
    if(reports.size == 0) return 0L
    stock + flow * getRate
  }
  
  override def toString :String = {
    if(reports.size == 0) return ""
    val header = Html.toTrTh("年度", "解価", "NetC", "アク", "純利", "FCF", "粗率", "営率", "経率", "純率")
    """<h3>EDINET 有価証券報告書</h3>""" +
    Html.toTable(header :: reports.reverse)
  }
}

object Company{
  def apply(code:String) = new Company(code)
}