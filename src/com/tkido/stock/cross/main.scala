package com.tkido.stock.cross

import com.tkido.stock.Config
import com.tkido.tools.Log
import com.tkido.tools.Text

object Main extends App {
  Log open Config.logLevel
  
  val taisyakuCodes = ParserJpx()
  val kabuComCodes = ParserKabuCom()
  val sbiCodes = ParserSbi()
  
  val shortableCodes = (taisyakuCodes | kabuComCodes | sbiCodes).toList.sorted
  
  val data =
    if(Log.isDebug)
      shortableCodes.map(Processor(_))
    else
      shortableCodes.par.map(Processor(_))
  Text.write("data/cross/result.txt", data.mkString("\n"))
  
  Log close
}