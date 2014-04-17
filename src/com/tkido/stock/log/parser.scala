package com.tkido.stock.log

object Parser {
  import com.tkido.tools.Text
  
  def apply(path:String) :Map[String, List[List[String]]] = {
    val lines = Text.readLines(path)
    val arrs = lines.map(_.split("\t"))
    val rawData = arrs.groupBy(_(2))
    
    def convert(src:List[Array[String]]) :List[List[String]] = {
      def subconvert(arr:Array[String]) :List[String] = {
        val (date, trade, volume, price, amount) = (arr(0), arr(4).replaceFirst("株式", ""), arr(5), arr(6), arr(10))
        List(date, trade, volume, price, amount)
      }
      var rst = src.map(subconvert(_))
      rst
    }
    val data = rawData.mapValues(convert(_))
    data
  }
}