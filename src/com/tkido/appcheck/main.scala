package com.tkido.appcheck

object main extends App {
  import com.tkido.tools.Logger
  import com.tkido.tools.Tengine
  import com.tkido.tools.Text
  
  Logger.level = Config.logLevel
  
  val targets = TargetParser("data/appcheck/table.txt")
  val reports = targets.map(Processor(_))
  
  Logger.info(reports.mkString("\n"))
  
  //val result = Result("1", "2014-10-24T05:38:12-07:00")
  //Logger.debug(result)
  
  val te = Tengine("data/appcheck/template.html")
  val context = Map("data" -> reports.mkString("\n"))
  Text.write("data/appcheck/report.html", te(context))
  
  Logger.close()
}