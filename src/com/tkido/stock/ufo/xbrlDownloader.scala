package com.tkido.stock.ufo

object XbrlDownloader {
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def download(code:String) {  
    code match {
      case reJp() => XbrlDownloaderJp.download(code)
      case _ => ()
    }
  }
}