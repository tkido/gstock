package com.tkido.stock.spider

import com.tkido.stock.reJpStockCode
import com.tkido.stock.reUsTickerSymbol
import com.tkido.tools.Log

object Spider {
  def apply(code:String) :Map[String, String] = {
    Log d s"Spider Spidering ${code}"
    code match {
      case reJpStockCode() => SpiderJp(code)
      case reUsTickerSymbol() => SpiderUs(code)
    }
  }
}