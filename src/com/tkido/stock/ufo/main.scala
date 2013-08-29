package com.tkido.stock.ufo

object main extends App {
  import com.tkido.tools.TextFile
  
  Logger.level = Config.loglevel
  
  val codes = TextFile.readLines("data/ufo/table.txt")
  for (code <- codes) XbrlDownloader.download(code)
  
  Logger.close()
}
