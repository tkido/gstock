package com.tkido.tools

import com.ibm.icu.text.Normalizer
import com.ibm.icu.text.Transliterator

object main extends App {
  val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
  
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
    val tmp1 = s.replaceAll("""・|　|＆|ホールディングス?|コーポレーション|カンパニー|グループ|本社|ジャパン$""", "")
    val tmp2 = Normalizer.normalize(tmp1, Normalizer.NFKC)
    if(tmp2.size > 10){
      transliterator.transliterate(tmp2)
    }else{
      tmp2
    }
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
