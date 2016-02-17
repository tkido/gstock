package com.tkido.stock.patrol

import com.tkido.stock.Config
import com.tkido.tools.Log
import com.tkido.tools.Text

object Main extends App {
  Log open Config.logLevel
  
  val lastNumber = Config.buildNumber.last.toString
  val patrolCodes = Parser("data/patrol/table.txt").filter(c => c.endsWith(lastNumber)).toSet
  val excludeCodes = Parser("data/patrol/exclude.txt").toSet
  val codes = (patrolCodes &~ excludeCodes).toList
  
  val data =
    if(Log.isDebug)
      codes.map(Processor(_))
    else
      codes.par.map(Processor(_))
  
  val result = data.collect{case Some(s) => s}.mkString("\n")
  Text.write("data/patrol/result.txt", result)
  Text.write("data/ress/table.txt", result)
  
  Log close
}
