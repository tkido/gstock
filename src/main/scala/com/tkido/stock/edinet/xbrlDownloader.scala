package com.tkido.stock.edinet

import com.tkido.stock.reJpStockCode

object XbrlDownloader {
  def apply(code:String) {
    code match {
      case reJpStockCode() => XbrlDownloaderJp(code)
      case _ => ()
    }
  }
}