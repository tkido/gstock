package com.tkido.stock.log

import com.tkido.tools.Html
import com.tkido.tools.Log

object Reporter {
  Log d "log.Reporter parse rireki.txt"
  val dataMap = Parser("data/log/rireki.txt")
  
  def apply(code:String) :String = {
    if(!dataMap.contains(code)) return ""
    
    val header = Html.toTrTh("日付", "取引", "数量", "価格", "金額")
    val list = dataMap(code).map(Html.toTrTd(_: _*))
    """<h3>売買ログ</h3>""" +
    Html.toTable(header :: list.reverse)
  }
}