package com.tkido.tools

object main extends App {
  import scala.util.control.Exception._
  
  Log open Log.DEBUG
  
  Log d "Test Start"
  
  val op = allCatch opt retry({ println("trying..."); "13".toInt })
  //val op = allCatch opt retry({ println("trying..."); "a".toInt })
 
  
  println(op.isDefined)
  
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
  Log.close()
}
