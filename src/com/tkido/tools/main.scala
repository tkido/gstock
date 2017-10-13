package com.tkido.tools

import com.ibm.icu.text.Transliterator
import com.ibm.icu.text.Normalizer

object main extends App {
  Log.logging(Log.DEBUG, main)
  
  def main() {
    Log d "Test Start"
    
    """
    //val op = allCatch opt retry({ println("trying..."); "13".toInt })
    val op = retry({ println("trying..."); "a".toInt })
    
    println(op.isDefined)
    """
    
    val lines = Text.readLines("data/test/table.txt")
    for(line <- lines){
      //println(line)
      println(tidyName(line))
    }
  }

  def tidyName(s:String) :String = {
    val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
    val tmp = s.replaceAll("""・|　|＆|ホールディングス?|コーポレーション|カンパニー|グループ|本社|ジャパン$""", "")
    //transliterator.transliterate(tmp)
    Normalizer.normalize(tmp, Normalizer.NFKC)
  }
  
  
  
  
  /*
  def retryIf[T](p: => Boolean)(f: => T):T =
    try{
      f
    }catch{
      case e:Throwable  =>
        if(p)
          retryIf(p)(f)
        else
          throw e
    }
  */
  
}
