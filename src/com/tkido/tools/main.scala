package com.tkido.tools

object main extends App {
  Log open Log.DEBUG
  
  Log d "Test Start"
  
  def retry[T](f: => T, max:Int = 3, interval:Int = 1000, count:Int = 1):T = {
    try{
      f
    }catch{
      case e:Throwable  =>
        Log d e
        if(count < max){
          Thread.sleep(interval)
          retry(f, max, interval, count+1)
        }else{
          throw e
        }
    }
  }
  retry({ println("trying..."); "a".toInt })
  
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
