package com.tkido.stock.patrol

object Parser {
  import com.tkido.tools.Text
  
  val reJp = """[0-9]{4}""".r
  
  def apply(path:String) :List[String] = {
    Text.readLines(path).filter{
      case reJp() => true
      case _      => false
    }
  }
}