package com.tkido.stock.rss

import com.tkido.stock.tdnet.XbrlDownloader
import com.tkido.tools.Logger
import com.tkido.tools.Text

object main extends App {
  Logger.level = com.tkido.stock.Config.logLevel
  
  val codes = Parser("data/rss/table.txt")
  val range = Range(Config.offset, Config.offset + codes.size)
  val pairs = codes zip range
  
  val data =
    if(Logger.level == Logger.DEBUG)
      pairs.map(Processor(_))
    else
      pairs.par.map(Processor(_))
  
  Text.write("data/rss/result.txt", data.mkString("\n"))
  Text.write("data/rss/downloaded.txt", XbrlDownloader.getResult)
  
  Logger.close()
}