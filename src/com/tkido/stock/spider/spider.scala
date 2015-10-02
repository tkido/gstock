package com.tkido.stock.spider

object Spider {
  import com.tkido.tools.Log
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) :Map[String, String] = {
    Log d s"Spider Spidering ${code}"
    code match {
      case reJp() => SpiderJp(code)
      case reUs() => SpiderUs(code)
    }
  }
}