package com.tkido.appcheck

object Processor {
  import com.tkido.tools.Logger
  
  def apply(target:Target) :String = {
    Logger.debug(target)
    
    val result = Checker(target)
    Logger.debug(result)
    
    val results = ResultParser(target)
    Logger.debug(results)
    
    if(result.rank != results.head.rank){
      
    }
    
    ""
  }
  
}