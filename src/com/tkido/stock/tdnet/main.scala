package com.tkido.stock.tdnet

import com.tkido.tools.Log

object main extends App {
  Log.logging(Log.DEBUG, main)
  
  def main() {
    Log d "Test Start"  
    XbrlDownloaderJp("9984")
  }
}
