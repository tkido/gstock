package com.tkido.stock.cross

import com.tkido.tools.Text

object ParserJpx {
  val regx = """^([0-9]{4}).*貸借銘柄$""".r
  
  def apply(path:String) :List[String] = {
    Text.readLines(path, "shift-jis").collect{
      case regx(code) => code
    }
  }
}