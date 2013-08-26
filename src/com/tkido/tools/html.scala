package com.tkido.tools

class Html(url:String, charset:String) {
  import scala.io.Source
  import scala.util.matching.Regex
  
  val lines = Source.fromURL(url, charset).getLines.toList
  
  def getPreviousLineOf(rgex:Regex) :String = {
    var last = ""
    for(line <- lines){
      if(rgex.findFirstIn(line).isDefined)
        return Html.removeTags(last)
      last = line
    }
    ""
  }
  
  def getNextLineOf(rgex:Regex) :String = {
    var flag = false
    
    for(line <- lines){
      if(flag)
        return Html.removeTags(line)
      if(rgex.findFirstIn(line).isDefined)
        flag = true
    }
    ""
  }
  
  def getLineOf(rgex:Regex) :String = {
    for(line <- lines){
      if(rgex.findFirstIn(line).isDefined)
        return line
    }
    ""
  }
  
  def getGroupOf(rgex:Regex) :String = {
    val opt = lines.collectFirst{ case rgex(m) => m }
    opt.getOrElse("")
  }
  
}

object Html{
  def apply(url:String) = new Html(url, "UTF-8")
  def apply(url:String, charset:String) = new Html(url, charset)
  
  def removeTags(string:String) :String =
    string.replaceAll("""<.*?>""", "").trim

}