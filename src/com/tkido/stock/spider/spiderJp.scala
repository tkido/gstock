package com.tkido.stock.spider

object SpiderJp {
  import com.tkido.tools.Logger
  
  def apply(code:String) :Map[String, String] = {
    Logger.debug("SpiderJp Spidering ", code)
    
    SpiderJpStockholder(code) ++
    SpiderJpConsolidate(code) ++
    SpiderJpProfile(code) ++
    SpiderJpHistory(code) ++
    SpiderJpDetail(code) ++
    Map("国" -> "JP")
  }
}