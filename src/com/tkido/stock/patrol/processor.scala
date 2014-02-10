package com.tkido.stock.patrol

object Processor {
  import com.tkido.stock.edinet
  import com.tkido.stock.rss.ChartMaker
  import com.tkido.stock.rss.Company
  import com.tkido.stock.tdnet
  
  def apply(pair:Pair[String, Int]) :String = {
    val (code, row) = pair
    
    edinet.XbrlDownloader(code)
    tdnet.XbrlDownloader(code)
    
    val company = Company(code, row)
    ChartMaker(company)
    company.toString
  }
  
}