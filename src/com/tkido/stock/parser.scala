package com.tkido.stock

object Parser {
  import com.tkido.tools.Text
  
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