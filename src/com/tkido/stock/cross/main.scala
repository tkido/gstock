package com.tkido.stock.cross

import com.tkido.stock.Config
import com.tkido.tools.Log
import com.tkido.tools.Text

object Main extends App {
  Log open Config.logLevel
  
  val taisyakuCodes = ParserJpx()
  
  
  /*
  val range = Range(Config.offset, Config.offset + codes.size)
  val pairs = codes zip range
  
  val data =
    if(Log.isDebug)
      pairs.map(Processor(_))
    else
      pairs.par.map(Processor(_))
  */
  Text.write("data/cross/result.txt", taisyakuCodes.mkString("\n"))
  
  Log close
}