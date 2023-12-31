package com.tkido.stock.tdnet

import com.tkido.tools.Log
import java.io.File
import scala.xml._

object XbrlParser {
  def apply(path :String) :Report[Long] = {
    if(path.endsWith(".xbrl"))
      XbrlParserXbrl(path)
    else
      XbrlParserInline(path)
  }
}