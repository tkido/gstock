package com.tkido.stock.rss

import com.tkido.stock.Config
import com.tkido.stock.tdnet.XbrlDownloader
import com.tkido.tools.Log
import com.tkido.tools.Text

object Main extends App {
  Log.logging(Config.logLevel, main)
  
  def main() {
    val codes = Parser("data/rss/table.txt")
    val range = Range(Config.offset, Config.offset + codes.size)
    val pairs = codes zip range
    
    val data =
      if(Log.isInfo)
        pairs.map(Processor(_))
      else
        pairs.par.map(Processor(_))
    
    Text.write("data/rss/result.txt", data.mkString("\n"))
    Text.write("data/rss/downloaded.txt", XbrlDownloader.getResult)
  }
}