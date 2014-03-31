package com.tkido.stock.edinet

class Report(path:String) {
  import com.tkido.tools.Html
  import java.io.File
  
  def toYear(file:File) :Int = {
    val name = file.getName
    if(name.take(4) == "jpfr"){
      name.slice(20, 24).toInt
    }else{
      name.slice(31, 35).toInt
    }
  }
  
  val file = new File(path)
  val year = toYear(file)
  val data = XbrlParser(path)
  
  def netIncome = data("NetIncome")
  
  def sumItems(items:Map[String, Int]) :Long =
    items.map(p => data.getOrElse(p._1, 0L) * p._2).sum / 100
  
  def breakupValue = sumItems(Report.breakupItems)
  def netCash      = sumItems(Report.netCashItems)
  def accruals     = sumItems(Report.accrualsItems)
  def freeCashFlow = sumItems(Report.freeCashFlowItems)
  
  def ratioItems(numerator:String, denominator:String) :Option[Double] = {
    if(data.contains(numerator) && data.contains(denominator))
      Some(data(numerator).toDouble / data(denominator).toDouble)
    else None
  }
  def grossProfitRatio     = ratioItems("GrossProfit",     "NetSales")
  def operatingProfitRatio = ratioItems("OperatingIncome", "NetSales")
  def ordinaryProfitRatio  = ratioItems("OrdinaryIncome",  "NetSales")
  def netProfitRatio       = ratioItems("NetIncome",       "NetSales")
  
  override def toString =
    Html.toTrTd(year,
                breakupValue,
                netCash,
                accruals,
                netIncome,
                freeCashFlow,
                grossProfitRatio,
                operatingProfitRatio,
                ordinaryProfitRatio,
                netProfitRatio )
}

object Report{
  import com.tkido.tools.Properties
  
  def apply(path:String) = new Report(path)
  
  val breakupItems      = Properties("data/edinet/breakup.properties",      _.toInt)
  val netCashItems      = Properties("data/edinet/netcash.properties",      _.toInt)
  val accrualsItems     = Properties("data/edinet/accruals.properties",     _.toInt)
  val freeCashFlowItems = Properties("data/edinet/freecashflow.properties", _.toInt)
}