package com.tkido.stock.patrol

object main extends App {
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  
  Logger.level = com.tkido.stock.Config.logLevel
  
  val number = Config.buildNumber.last.toString
  
  val patrolCodes = Parser("data/patrol/table.txt").toSet
  val excludeCodes = Parser("data/patrol/exclude.txt").toSet
  val rssCodes = Parser("data/rss/table.txt").toSet
  val codes = (patrolCodes &~ rssCodes &~ excludeCodes).toList.filter(c => c.endsWith(number))
  
  val data =
    if(Logger.level == Logger.DEBUG)
      codes.map(Processor(_))
    else
      codes.par.map(Processor(_))
  
  Text.write("data/patrol/result.txt", data.collect{case Some(s) => s}.mkString("\n"))
  
  Logger.close()
}
