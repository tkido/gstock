package com.tkido.stock.ufo

object main extends App {
  import com.tkido.tools.Text
  
  Logger.level = Config.loglevel
  
  val codes = Text.readLines("data/ufo/table.txt")
  for (code <- codes) XbrlDownloader.download(code)
  
  Logger.close()
}
