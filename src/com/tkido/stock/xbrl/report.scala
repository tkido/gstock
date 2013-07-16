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
    var sum = BigInt(0)
    for((key, value) <- items)
      if(data.contains(key))
        sum += data(key) * value 
    sum / 100
  }
  
  override def toString = {
    val buf = new StringBuilder
    buf ++= "%s\t".format(year)
    buf ++= "%s\t".format(breakupValue)
    buf ++= "%s\t".format(netCash)
    buf ++= "%s\t".format(accruals)
    buf ++= "%s\t".format(netIncome)
    buf ++= "%s\t".format(freeCashFlow)
    buf ++= "\n"
    buf.toString
  }
}

object Report{
  def apply(path:String) = new Report(path)
}