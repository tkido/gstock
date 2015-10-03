package com.tkido

package object stock {
  val reJpStockCode = """[0-9]{4}""".r
  val reUsTickerSymbol = """[A-Z]{1,5}""".r
}
