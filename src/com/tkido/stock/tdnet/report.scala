package com.tkido.stock.tdnet

class Report(path:String) {
  import com.tkido.tools.Html
  import com.tkido.tools.Logger
  
  
  val data = XbrlParser(path)
  
  def netIncome = data("NetIncome")


}

object Report{
  def apply(path:String) = new Report(path)
}