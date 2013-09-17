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
  
  def getGroupOf(rgex:Regex) :String =
    lines.collectFirst{ case rgex(s) => s }.getOrElse("")
  
}

object Html{
  def apply(url:String) = new Html(url, "utf-8")
  def apply(url:String, charset:String) = new Html(url, charset)
  
  def removeTags(string:String) :String =
    string.replaceAll("""<.*?>""", "").trim
  
  //for publishing
  def toTable(rows:List[Any]) :String =
    rows.map(toHtml).mkString("""<table border="2" class="numbers"><tbody>""", "", "</tbody></table>")
  def toTrTh(args: Any*) :String =
    args.toList.map(toHtml).mkString("<tr><th>", "</th><th>", "</th></tr>")
  def toTrTd(args: Any*) :String =
    args.toList.map(toHtml).mkString("<tr><td>", "</td><td>", "</td></tr>")
  
  def toHtml(arg:Any) :String = {
    arg match {
      case x:String => x
      case x:BigInt => toHtml(x)
      case x:Double => toHtml(x)
      case Some(x)  => toHtml(x)
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
  
  private def toHtml(arg:BigInt) :String = {
    def sub(str:String, col:Int) :String = {
      if(str.size > 4) sub(str.dropRight(3), col+3)
      else str + (col match {
        case  0 => ""
        case  3 => "K"
        case  6 => "M"
        case  9 => "B"
        case 12 => "T"
        case 15 => "Q"
        case _  => "MUST_NOT_HAPPEN!!"
      })
    }
    val number = sub(arg.abs.toString, 0)
    val klass = if(arg < 0) """ class="minus"""" else ""
    val sign =  if(arg < 0) "-" else ""
    "<span%s>%s%s</span>".format(klass, sign, number)
  }

}