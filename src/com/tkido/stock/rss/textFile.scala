package com.tkido.stock.rss

object TextFile {
  import scala.io.Source
  import java.io.PrintWriter
  
  def readLines(path:String, charset:String) :List[String] = {
    val s = Source.fromFile(path, charset)
    val lines = try s.getLines.toList finally s.close
    lines.map(_.stripLineEnd)
  }
  def readLines(path:String) :List[String] =
    readLines(path, "UTF-8")
  
  def writeString(path:String, data:String) {
    val out = new PrintWriter(path)
    out.println(data)
    out.close
  }  
  
}