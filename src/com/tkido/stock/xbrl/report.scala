package com.tkido.stock.xbrl

class Report(path:String) {
  import java.io.File
  
  val year = new File(path).getName.slice(20, 24).toInt
  val data = XbrlParser.parse(path)
  
  def breakupValue = sumItems(XbrlParser.breakupData)
  def netCash      = sumItems(XbrlParser.netCashData)
  def accruals     = sumItems(XbrlParser.accrualsData)
  def freeCashFlow = sumItems(XbrlParser.freeCashFlowData)
  
  def netIncome = data("NetIncome")
  
  def sumItems(items:Map[String, Int]) :BigInt = {
    val nums =
      for((key, value) <- items if data.contains(key))
        yield data(key) * value
    nums.sum / 100
  }
  
  override def toString = {
    val list = List(year, breakupValue, netCash, accruals, netIncome, freeCashFlow)
    list.map(_.toString).mkString("\t")
  }
  
  def toTr :String = {
    val list = List(year, breakupValue, netCash, accruals, netIncome, freeCashFlow)
    list.map(_.toString).mkString("<tr><td>", "</td><td>", "</td></tr>")
  }
}

object Report{
  def apply(path:String) = new Report(path)
}