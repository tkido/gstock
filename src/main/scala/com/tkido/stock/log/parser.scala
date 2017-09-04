package com.tkido.stock.log

import com.tkido.tools.Text

object Parser {
  def apply(path:String) :Map[String, List[List[String]]] = {
    val lines = Text.readLines(path)
    val arrs = lines.map(_.split("\t"))
    val rawData = arrs.groupBy(_(2))
    
    def convert(src:List[Array[String]]) :List[List[String]] = {
      def subconvert(arr:Array[String]) :List[String] = {
        val (date, trade, volume, price, amount) = (arr(0), arr(4), arr(5), arr(6), arr(10))
        List(date, trade.replaceFirst("株式", ""), volume, price, amount)
      }
      src.map(subconvert(_))
    }
    rawData.mapValues(convert(_))
  }
}