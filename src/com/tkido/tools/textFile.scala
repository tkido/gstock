package com.tkido.tools

object TextFile {
  import scala.io.Source
  import java.io.FileOutputStream
  import java.io.OutputStreamWriter
  import java.io.PrintWriter

  def readLines(path:String, charset:String) :List[String] = {
    val s = Source.fromFile(path, charset)
    val lines = try s.getLines.toList finally s.close
    lines.map(_.stripLineEnd)
  }
  def readLines(path:String) :List[String] =
    readLines(path, "UTF-8")

  def read(path:String, charset:String) :String =
    readLines(path, charset).mkString("\n")
  def read(path:String) :String =
    read(path, "UTF-8")

  def write(path:String, data:String, charset:String) {
    val fos = new FileOutputStream(path)
    val osw = new OutputStreamWriter(fos, charset)
    val pw = new PrintWriter(osw)
    pw.println(data)
    pw.close
  }  
  def write(path:String, data:String) {
    write(path, data, "UTF-8")
  }
}