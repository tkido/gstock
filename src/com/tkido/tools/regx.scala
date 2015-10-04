package com.tkido.tools

object Regx {
  import scala.util.matching.Regex
  
  def matchedAndRest(regp:String, str:String) :(String, String) = {
    val regx = regp.mkString("^.*?(", "", ").*$").r
    str match{
      case regx(s) => (s, str.replaceFirst(Regex.quote(s), ""))
      case _ => ("", str)
    }
  }
  
  def collectMatched(regp:String, str:String) :List[String] = {
    def go(regp:String, str:String, list:List[String]) :List[String] = {
      val regx = regp.mkString("^.*?(", "", ").*$").r
      str match{
        case regx(s) => s :: go(regp, str.replaceFirst(Regex.quote(s), ""), list)
        case _ => list
      }
    }
    go(regp, str, List())
  }
}