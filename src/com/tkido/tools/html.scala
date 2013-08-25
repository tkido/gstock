package com.tkido.tools

class Html(url:String, charset:String) {
  import scala.io.Source
  import scala.util.matching.Regex
  
  val lines = Source.fromURL(url, charset).getLines.toList
  
  def getPreviousLineOf(rgex:Regex) :String = {
    var last = ""
    var target = ""
    for(line <- lines){
      if(rgex.findFirstIn(line).isDefined)
        target = last
      last = line
    }
    Html.removeTags(target)
  }
  
  def getNextLineOf(rgex:Regex) :String = {
    var flag = false
    var target = ""
    
    for(line <- lines){
      if(flag){
        target = line
        flag = false
      }
      if(rgex.findFirstIn(line).isDefined)
        flag = true
    }
    Html.removeTags(target)
  }
  
  def getLineOf(rgex:Regex) :String = {
    var target = ""
    for(line <- lines){
      if(rgex.findFirstIn(line).isDefined)
        target = line
    }
    target
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