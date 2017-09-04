package com.tkido.stock.tdnet

import com.tkido.stock.reJpStockCode
import scala.collection.mutable.ListBuffer

object XbrlDownloader {
  val buf = ListBuffer.empty[String]
  
  def apply(code:String) {
    code match {
      case reJpStockCode() => XbrlDownloaderJp(code)
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