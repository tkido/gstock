package com.tkido.stock.xbrl

class Report(path:String) {
  import com.tkido.tools.Html
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
  
  def toTr :String = {
    val list = List(BigInt(year), breakupValue, netCash, accruals, netIncome, freeCashFlow)
    list.map(Html.round(_)).mkString("<tr><td>", "</td><td>", "</td></tr>")
  }
}

object Report{
  def apply(path:String) = new Report(path)
}