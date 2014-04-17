package com.tkido.stock.log

object main extends App {
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  
  Logger.level = Logger.DEBUG
  
  Logger.debug(Reporter("9795"))
  
  Logger.close()
}
