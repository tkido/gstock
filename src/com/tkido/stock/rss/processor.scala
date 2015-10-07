package com.tkido.stock.rss

import com.tkido.stock.edinet
import com.tkido.stock.page.PageMaker
import com.tkido.stock.tdnet

object Processor {
  def apply(pair:Tuple2[String, Int]) :String = {
    val (code, row) = pair
    
    edinet.XbrlDownloader(code)
    tdnet.XbrlDownloader(code)
    
    val company = Company(code, row)
    PageMaker(company.data)
    company.toRssString
  }
  
}