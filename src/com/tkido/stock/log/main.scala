package com.tkido.stock.log

object main extends App {
  import com.tkido.tools.Log
  import com.tkido.tools.Text
  
  Log.level = Log.DEBUG
  
  Log d Reporter("9795")
  
  Log.close()
}
