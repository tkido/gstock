package com.tkido.tools

class Tengine(path:String) {
  val lines = Text.readLines(path)
  val re = """#\{(.*?)\}""".r
  
  def render(data:Map[String, String]) :String = {
    lines.map(line =>
      re.replaceAllIn(line, m => data.getOrElse(m.group(1), ""))
    ).mkString
  }
}

object Tengine {
  def apply(path:String): Tengine =
    new Tengine(path)
}