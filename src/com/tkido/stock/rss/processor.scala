package com.tkido.stock.rss

object Processor {
  import com.tkido.stock.edinet
  import com.tkido.stock.page.PageMaker
  import com.tkido.stock.tdnet
  
  def apply(pair:Pair[String, Int]) :String = {
    val (code, row) = pair
    
    edinet.XbrlDownloader(code)
    tdnet.XbrlDownloader(code)
    
    val company = Company(code, row)
    //PageMaker(company)
    company.toRssString
  }
  
}