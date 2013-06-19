package com.tkido.stock.xbrl

class Company(code:String) {
  import java.io.File
  
  //XbrlParser.parse("data/xbrl/3085/XBRL_20130617_105616/S000D3P0/jpfr-asr-E03513-000-2012-12-31-01-2013-03-25.xbrl")
  val filesHere = (new java.io.File(".").listFiles)
  for(file <- filesHere) println(file)
  
  override def toString = code
}

object Company{
  def apply(code:String) = new Company(code)
}