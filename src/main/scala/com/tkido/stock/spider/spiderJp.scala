package com.tkido.stock.spider

import com.tkido.tools.Log

object SpiderJp {
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