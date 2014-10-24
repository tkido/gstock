package com.tkido.appcheck

object main extends App {
  import com.tkido.stock.Config
  import com.tkido.tools.Logger
  
  Logger.level = Config.logLevel
  
  val targets = Parser("data/appcheck/table.txt")
  val results = targets.map(Processor(_))
  
  Logger.debug(results)
  
  Logger.close()
}