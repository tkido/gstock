package com.tkido.stock.spider

object SpiderUs {
  import com.tkido.tools.Logger
  
  def apply(code:String) :Map[String, String] = {
    Logger.debug("SpiderUs Spidering ", code)
    
    SpiderUsSummary(code) ++
    SpiderUsProfile(code) ++
    SpiderUsKeyStatistics(code) ++
    Map("国" -> "US")
  }  
}