package com.tkido.stock.xbrl

class Company(code:String) {
  import java.io.File
  
  val filesHere = (new java.io.File(".").listFiles)
  for(file <- filesHere) println(file)
  
  //val data = XbrlParser.parse("data/xbrl/3085/XBRL_20130617_105616/S000D3P0/jpfr-asr-E03513-000-2012-12-31-01-2013-03-25.xbrl")
  val data = XbrlParser.parse("data/xbrl/5918/XBRL_20130619_202619/S000B8E7/jpfr-asr-E01364-000-2012-03-31-01-2012-06-29.xbrl")
  
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
  
  override def toString = code
}

object Company{
  def apply(code:String) = new Company(code)
}