package com.tkido.stock.patrol

object main extends App {
  import com.tkido.stock.Config
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  
  Logger.level = Config.logLevel
  
  val patrolCodes = Parser("data/patrol/table.txt").toSet
  val rssCodes = Parser("data/table.txt").toSet
  val codes = (patrolCodes &~ rssCodes).toList
  
  val data =
    if(Logger.level == Logger.DEBUG)
      codes.map(Processor(_))
    else
      codes.par.map(Processor(_))
  
  Text.write("data/patrol/result.txt", data.collect{case Some(s) => s}.mkString("\n"))
  
  Logger.close()
}
