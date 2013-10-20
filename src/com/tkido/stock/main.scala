package com.tkido.stock

object main extends App {
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  
  Logger.level = Config.logLevel
  
  val codes = Text.readLines("data/table.txt")
  val range = Range(Config.offset, Config.offset + codes.size)
  val pairs = codes zip range
  
  val data =
    if(Logger.level == Logger.DEBUG)
      pairs.map(Processor(_))
    else
      pairs.par.map(Processor(_))
  
  Text.write("data/result.txt", data.mkString("\n"))
  
  Logger.close()
}
