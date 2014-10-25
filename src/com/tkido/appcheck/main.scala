package com.tkido.appcheck

object main extends App {
  import com.tkido.tools.Logger
  import java.util.Date
  
  Logger.level = Config.logLevel
  
  val targets = Parser("data/appcheck/table.txt")
  val results = targets.map(Processor(_))
  
  //val result = Result("1", "2014-10-24T05:38:12-07:00")
  //Logger.debug(result)
  
  Logger.close()
}