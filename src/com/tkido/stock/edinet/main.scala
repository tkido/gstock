package com.tkido.stock.edinet

object main extends App {
  import com.tkido.tools.Log
  Log open Log.DEBUG
  Log d "Test Start"
  
  XbrlDownloaderJp("9984")
  
  
  Log.close()
}
