package com.tkido.stock.xbrl

class Report(path:String) {
  import com.tkido.tools.Html
  import java.io.File
  
  val year = new File(path).getName.slice(20, 24).toInt
  val data = XbrlParser.parse(path)
  
  def breakupValue = sumItems(Report.breakupData)
  def netCash      = sumItems(Report.netCashData)
  def accruals     = sumItems(Report.accrualsData)
  def freeCashFlow = sumItems(Report.freeCashFlowData)
  
  def netIncome = data("NetIncome")
  
  def ordinaryIncomeRatio :Double = {
    data("OrdinaryIncome").toDouble / data("NetSales").toDouble
  }
  
  def sumItems(items:Map[String, Int]) :BigInt = {
    val nums =
      for((key, value) <- items if data.contains(key))
        yield data(key) * value
    nums.sum / 100
  }
  
  override def toString =
    Html.toTrTd(year, breakupValue, netCash, accruals, netIncome, freeCashFlow, ordinaryIncomeRatio)
}

object Report{
  import com.tkido.tools.Text
  
  def apply(path:String) = new Report(path)
  
  val breakupData      = parseItems("data/xbrl/breakup_items.txt")
  val netCashData      = parseItems("data/xbrl/netcash_items.txt")
  val accrualsData     = parseItems("data/xbrl/accruals_items.txt")
  val freeCashFlowData = parseItems("data/xbrl/freecashflow_items.txt")
  
  def parseItems(path:String): Map[String, Int] = {
    def lineToPair(line:String) :Pair[String, Int] = {
      val arr = line.split("\t")
      arr(0) -> arr(1).toInt
    }
    def isValid(line:String) :Boolean =
      line.nonEmpty && line.head != '#'
    val lines = Text.readLines(path)
    lines.filter(isValid).map(lineToPair).toMap
  }
}