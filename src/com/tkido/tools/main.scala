package com.tkido.tools

object main extends App {
  Log.logging(Log.DEBUG, main)
  
  def main() {
    Log d "Test Start"
    //val op = allCatch opt retry({ println("trying..."); "13".toInt })
    val op = retry({ println("trying..."); "a".toInt })
    
    println(op.isDefined)
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
