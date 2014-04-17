package com.tkido.stock.log

object Reporter {
  import com.tkido.tools.Html
  import com.tkido.tools.Logger
  
  val data = Parser("data/log/rireki.txt")
  
  def apply(code:String) :String = {
    val header = Html.toTrTh("日付", "取引", "数量", "価格", "金額")
    val list = data(code).map(Html.toTrTd(_: _*))
    
    """<h3>売買ログ</h3>""" +
    Html.toTable(header :: list)
  }
}