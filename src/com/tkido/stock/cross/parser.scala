package com.tkido.stock.cross

import com.tkido.tools.Text

object ParserJpx {
  val regx = """^([0-9]{4}).*貸借銘柄$""".r
  
  def apply() :List[String] = {
    Text.readLines("data/cross/list.csv", "shift-jis").collect{
      case regx(code) => code
    }
  }
}


object ParserKabuCom {
  val regx = """^.*?([0-9]{4}).*$""".r
  
  def apply() :List[String] = {
    Text.readLines("data/cross/meigara_list.csv", "shift-jis").collect{
      case regx(code) => code
    }
  }
}