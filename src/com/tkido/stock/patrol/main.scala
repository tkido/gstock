package com.tkido.stock.patrol

object main extends App {
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  
  Logger.level = Config.logLevel
  
  val codes = Parser("data/patrol/table.txt")
  
  val data =
    if(Logger.level == Logger.DEBUG)
      codes.map(Processor(_))
    else
      codes.par.map(Processor(_))
  
  Text.write("data/patrol/result.txt", data.mkString("\n"))
  
  Logger.close()
}
