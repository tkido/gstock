package com.tkido.stock.xbrl

class Report(path:String) {
  import java.io.File
  val year = new File(path).getName.slice(20, 24).toInt
  val data = XbrlParser.parse(path)
  
  def breakupValue = sumItems(XbrlParser.breakupData)
  def netCash      = sumItems(XbrlParser.netCashData)
  def accruals     = sumItems(XbrlParser.accrualsData)
  
  def sumItems(items:Map[String, Int]) :BigInt = {
    var sum = BigInt(0)
    for((key, value) <- items)
      if(data.contains(key))
        sum += data(key) * value 
    sum / 100
  }
  def compare(that:Report) =
    year.compare(that.year)
  override def toString = path
}

object Report{
  def apply(path:String) = new Report(path)
}