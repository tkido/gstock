package com.tkido.stock.patrol

import com.tkido.stock.edinet
import com.tkido.stock.tdnet

object Processor {
  def apply(code:String) :Option[String] = {
    edinet.XbrlDownloader(code)
    tdnet.XbrlDownloader(code)
    
    if(Company(code))
      Some(code)
    else
      None
  }
  
}