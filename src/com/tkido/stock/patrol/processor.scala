package com.tkido.stock.patrol

object Processor {
  import com.tkido.stock.edinet
  import com.tkido.stock.rss.CompanyJpOther
  import com.tkido.stock.tdnet
  
  def apply(code:String) :String = {
    edinet.XbrlDownloader(code)
    tdnet.XbrlDownloader(code)
    
    val company = CompanyJpOther(code, 0)
    company.toString
  }
  
}