package com.tkido

import scala.util.control.Exception._

package object tools {
  
  implicit class MyString(s:String) {
    import scala.util.matching.Regex
    
    def matched(re:Regex) :Boolean =
      re.findFirstIn(s).isDefined
    
    def =~(re:Regex) :Boolean = matched(re)
    def !~(re:Regex) :Boolean = !matched(re)
  }
  
  def retry[T](f: => T, max:Int = 3, interval:Int = 1000) :Option[T] =
    allCatch opt retrySub(f, max, interval, 1)
  
  private def retrySub[T](f: => T, max:Int = 3, interval:Int = 1000, count:Int):T = {
    try{
      f
    }catch{
      case e:Throwable  =>
        Log f e
        if(count < max){
          Thread.sleep(interval)
          retrySub(f, max, interval, count+1)
        }else{
          throw e
        }
    }
  }
  
  def selfOrElse[T](source:T, default:T) :T = {
    if(source == null) default
    else source
  }
  
  def tryOrElse[T](func:() => T, default:T) :T = {
    try{
      func()
    }catch{
      case e:Throwable => {
        Log f e
        default
      }
    }
  }
  
}