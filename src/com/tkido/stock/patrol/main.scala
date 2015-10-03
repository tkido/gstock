package com.tkido.stock.patrol

import com.tkido.stock.Config
import com.tkido.tools.Log
import com.tkido.tools.Text

object main extends App {
  Log.level = Config.logLevel
  
  val number = Config.buildNumber.last.toString
  
  val patrolCodes = Parser("data/patrol/table.txt").toSet
  val excludeCodes = Parser("data/patrol/exclude.txt").toSet
  val rssCodes = Parser("data/rss/table.txt").toSet
  val codes = (patrolCodes &~ rssCodes &~ excludeCodes).toList.filter(c => c.endsWith(number))
  
  val data =
    if(Log.level == Log.DEBUG)
      codes.map(Processor(_))
    else
      codes.par.map(Processor(_))
  
  Text.write("data/patrol/result.txt", data.collect{case Some(s) => s}.mkString("\n"))
  
  Log.close()
}
