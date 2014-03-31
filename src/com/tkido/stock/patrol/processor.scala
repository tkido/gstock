package com.tkido.stock.patrol

object Processor {
  import com.tkido.stock.edinet
  import com.tkido.stock.tdnet
  
  def apply(code:String) :Option[String] = {
    edinet.XbrlDownloader(code)
    tdnet.XbrlDownloader(code)
    
    if(Company(code).isGood)
      Some(code)
    else
      None
  }
  
}