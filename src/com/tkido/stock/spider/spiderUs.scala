package com.tkido.stock.spider

object SpiderUs {
  import com.tkido.tools.Log
  
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderUs Spidering ${code}"
    
    SpiderUsSummary(code) ++
    SpiderUsProfile(code) ++
    SpiderUsKeyStatistics(code) ++
    Map("国" -> "US")
  }  
}