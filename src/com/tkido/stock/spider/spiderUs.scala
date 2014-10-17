package com.tkido.stock.spider

object SpiderUs {
  import com.tkido.tools.Logger
  
  def apply(code:String) :Map[String, String] = {
    Logger.debug("Spidering ", code)
    Map()
  }
}