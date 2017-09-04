package com.tkido.stock.patrol

import com.tkido.stock.reJpStockCode
import com.tkido.tools.Text

object Parser {
  def apply(path:String) :List[String] = {
    Text.readLines(path).filter{
      case reJpStockCode() => true
      case _ => false
    }
  }
}