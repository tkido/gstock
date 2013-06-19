package com.tkido.stock.xbrl

class Company(code:String) {
  import java.io.File
  
  val filesHere = (new java.io.File(".").listFiles)
  for(file <- filesHere) println(file)
  
  val data = XbrlParser.parse("data/xbrl/3085/XBRL_20130617_105616/S000D3P0/jpfr-asr-E03513-000-2012-12-31-01-2013-03-25.xbrl")
  
  def breakupValue() :BigInt = {
    var sum = BigInt(0)
    val items = XbrlParser.breakupData
    
    for((key, value) <- items)
      if(data.contains(key))
        sum += data(key) * value 
    sum / 100
  }
  
  def netCash() :BigInt = {
    var sum = BigInt(0)
    val items = XbrlParser.netCashData
    
    for((key, value) <- items)
      if(data.contains(key))
        sum += data(key) * value 
    sum / 100
  }
  
  def accruals() :BigInt = {
    var sum = BigInt(0)
    val items = XbrlParser.accrualsData
    
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