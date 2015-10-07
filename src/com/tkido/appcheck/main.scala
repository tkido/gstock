package com.tkido.appcheck

import com.tkido.stock.Config
import com.tkido.tools.Log
import com.tkido.tools.Tengine
import com.tkido.tools.Text

object Main extends App {
  Log open Config.logLevel
  
  val targets = TargetParser("data/appcheck/table.txt")
  val reports = targets.map(Processor(_))
  
  Log i reports.mkString("\n")
  
  //val result = Result("1", "2014-10-24T05:38:12-07:00")
  //Log d result
  
  val te = Tengine("data/appcheck/template.html")
  val context = Map("data" -> reports.mkString("\n"))
  Text.write("data/appcheck/report.html", te(context))
  
  Log close
}