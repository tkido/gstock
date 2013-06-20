package com.tkido.stock.xbrl

class Report(path:String) {
  val data = XbrlParser.parse(path)
  
  def breakupValue() = sumItems(XbrlParser.breakupData)
  def netCash() = sumItems(XbrlParser.netCashData)
  def accruals() = sumItems(XbrlParser.accrualsData)
  
  def sumItems(items:Map[String, Int]) :BigInt = {
    var sum = BigInt(0)
    for((key, value) <- items)
      if(data.contains(key))
        sum += data(key) * value 
    sum / 100
  }
  override def toString = path
}

object Report{
  def apply(path:String) = new Report(path)
}