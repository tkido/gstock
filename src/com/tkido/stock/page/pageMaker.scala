package com.tkido.stock.page

object PageMaker {
  def apply(data:Map[String, String]){
    data("国") match {
      case "JP" => PageMakerJp(data)
      case "US" => PageMakerUs(data)
      case _    => ()
    }
  }
}