package com.tkido.stock.cross

import com.tkido.tools.Log
import com.tkido.tools.Text

object ParserJpx {
  val regx = """^([0-9]{4}).*貸借銘柄$""".r
  
  def apply() :Set[String] = {
    Text.readLines("data/cross/list.csv", "shift-jis").collect{
      case regx(code) => code
    }
    .toSet
  }
}


object ParserKabuCom {
  val regx = """^.*?([0-9]{4}).*$""".r
  
  def apply() :Set[String] = {
    Text.readLines("data/cross/meigara_list.csv", "shift-jis").collect{
      case regx(code) => code
    }
    .toSet
  }
}


object ParserSbi {
  val regx = """^"([0-9]{4}).*$""".r
  
  def apply() :Set[String] = {
    def isValid(line:String) :Boolean = {
      val arr = line.split(",")
      arr(6) == "\"◎\"" || arr(7) == "\"◎\""
    }
    
    Text.readLines("data/cross/CbsProductList.csv", "shift-jis")
    .drop(8)
    .filter(isValid)
    .collect{
      case regx(code) => code
    }
    .toSet
  }
}