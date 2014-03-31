package com.tkido.stock.edinet

object XbrlParser {
  import com.tkido.tools.Logger
  import java.io.File
  
  def apply(path :String) :Map[String, Long] = {
    Logger.debug(path)
    
    val name = new File(path).getName
    if(name.take(4) == "jpfr"){
      XbrlParserJpfr(path)
    }else{
      XbrlParserJpcrp(path)
    }
  }
}