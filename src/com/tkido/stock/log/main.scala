package com.tkido.stock.log

import com.tkido.tools.Log
import com.tkido.tools.Text

object main extends App {
  Log.level = Log.DEBUG
  
  Log d Reporter("9795")
  
  Log.close()
}
