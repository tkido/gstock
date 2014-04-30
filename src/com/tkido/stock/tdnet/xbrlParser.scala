package com.tkido.stock.tdnet

object XbrlParser {
  import com.tkido.tools.Logger
  import java.io.File
  import scala.xml._
  
  def apply(path :String) :Report[Long] = {
    if(path.endsWith(".xbrl"))
      XbrlParserXbrl(path)
    else
      XbrlParserInline(path)
  }
}