package com.tkido.tools

import scala.io.Source
import scala.util.matching.Regex
import scala.collection.mutable.{Set => MSet}
import scala.collection.mutable.{Map => MMap}

case class Search(name:String, regx:Regex, arg:Int, sanitize:String => String){
}

class Html(url:String, charset:String) {
  val lines = Text.readLines(url, charset)
  
  val trios =
    for((main, last, next) <- (lines, "" :: lines, lines.tail ::: List("")).zipped)
      yield (main, last, next)
  //Log d trios
  
  def search(args:List[Search]) :Map[String, String] = {
    val patterns:MSet[Search] = MSet()
    for(arg <- args) patterns += arg
    val results:MMap[String, String] = MMap()
    
    for((main, last, next) <- trios){
      if(patterns.isEmpty) return results.toMap
      
      for(pattern <- patterns){
        val opt = pattern.regx.findFirstMatchIn(main)
        if(opt.isDefined){
          val matched = pattern.arg match{
            case 0 => opt.get.group(1)
            case 1 => Html.removeTags(last)
            case 2 => Html.removeTags(next)
          }
          results(pattern.name) = pattern.sanitize(matched)
          patterns -= pattern
        }
      }
    }
    results.toMap
  }
  
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
  
  def getGroupOf(rgex:Regex) :String =
    lines.collectFirst{ case rgex(s) => s }.getOrElse("")
  
}

object Html{
  def apply(url:String) = new Html(url, "utf-8")
  def apply(url:String, charset:String) = new Html(url, charset)
  
  def removeTags(string:String) :String =
    string.replaceAll("""<.*?>""", "").trim
  
  def toTable(rows:List[Any]) :String =
    rows.map(toHtml).mkString("""<table border="2" class="numbers"><tbody>""", "", "</tbody></table>")
  def toTrTh(args: Any*) :String =
    args.toList.map(toHtml).mkString("<tr><th>", "</th><th>", "</th></tr>")
  def toTrTd(args: Any*) :String =
    args.toList.map(toHtml).mkString("<tr><td>", "</td><td>", "</td></tr>")
  
  def toHtml(arg:Any) :String = {
    arg match {
      case s:String => s
      case d:Long   => toHtml(d)
      case f:Double => toHtml(f)
      case Some(t)  => toHtml(t)
      case None     => "-"
      case _        => arg.toString
    }
  }
  
  private def toHtml(arg:Double) :String = {
    val number = (arg.abs * 100).round.toString + "%"
    val klass = if(arg < 0) """ class="minus"""" else ""
    val sign =  if(arg < 0) "-" else ""
    "<span%s>%s%s</span>".format(klass, sign, number)
  }
  
  private def toHtml(arg:Long) :String = {
    def round(str:String) :String = {
      val size = str.size
      val (head, tail) = str.take(3).splitAt(size % 3)
      val unit = (size - 1) / 3 match {
        case 0 => ""
        case 1 => "K"
        case 2 => "M"
        case 3 => "B"
        case 4 => "T"
        case 5 => "Q"
        case 6 => "q"
        case 7 => "X" //too large. maybe BUG.
      }
      val separator = if(head.isEmpty || tail.isEmpty) "" else "."
      head + separator + tail + unit
    }
    val number = round(arg.abs.toString)
    val klass = if(arg < 0) """ class="minus"""" else ""
    val sign =  if(arg < 0) "-" else ""
    "<span%s>%s%s</span>".format(klass, sign, number)
  }

}