package com.tkido.stock.spider

object SpiderJp {
  import com.tkido.tools.Log
  
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderJp Spidering ${code}"
    
    SpiderJpStockholder(code) ++
    SpiderJpConsolidate(code) ++
    SpiderJpProfile(code) ++
    SpiderJpHistory(code) ++
    SpiderJpDetail(code) ++
    Map("国" -> "JP")
  }
}