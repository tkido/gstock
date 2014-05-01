package com.tkido.stock.tdnet

object XbrlDownloader {
  import scala.collection.mutable.ListBuffer
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  val buf = ListBuffer.empty[String]
  
  def apply(code:String) {  
    code match {
      case reJp() => XbrlDownloaderJp(code)
      case _      => ()
    }
  }
  
  def add(url:String) {
    buf += url
  }
  
  def getResult :String = {
    buf.mkString("\n")
  }
}