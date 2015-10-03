package com.tkido.stock.rss

import com.tkido.stock.reJpStockCode
import com.tkido.stock.reUsTickerSymbol
import com.tkido.tools.Text

object Parser {
  def apply(path:String) :List[String] = {
    Text.readLines(path).filter{
      case reJpStockCode() => true
      case reUsTickerSymbol() => true
      case _ => false
    }
  }
}