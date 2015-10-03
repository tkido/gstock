package com.tkido.stock.rss

import com.tkido.tools.Text

object Parser {
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(path:String) :List[String] = {
    Text.readLines(path).filter{
      case reJp() => true
      case reUs() => true
      case _      => false
    }
  }
}