package com.tkido.stock.edinet

import com.tkido.tools.Log
import java.io.File

object XbrlParser {
  def apply(path :String) :Map[String, Long] = {
    Log d path
    
    val name = new File(path).getName
    if(name.take(4) == "jpfr"){
      XbrlParserJpfr(path)
    }else{
      XbrlParserJpcrp(path)
    }
  }
}