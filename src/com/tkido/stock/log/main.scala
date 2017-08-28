package com.tkido.stock.log

import com.tkido.tools.Log
import com.tkido.tools.Text

object main extends App {
  Log.logging(Log.DEBUG, main)
  
  def main() {
    Log d Reporter("9795")
  }
}
