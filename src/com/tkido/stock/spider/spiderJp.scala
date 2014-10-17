package com.tkido.stock.spider

object SpiderJp {
  import com.tkido.tools.Logger
  
  def apply(code:String) :Map[String, String] = {
    Logger.debug("SpiderJp Spidering ", code)
    
    SpiderJpStockholder(code) ++
    Map()
  }
}