package com.tkido.stock.spider

import com.tkido.tools.Log

object SpiderUs {
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderUs Spidering ${code}"
    
    SpiderUsSummary(code) ++
    SpiderUsProfile(code) ++
    SpiderUsKeyStatistics(code) ++
    Map("国" -> "US")
  }  
}