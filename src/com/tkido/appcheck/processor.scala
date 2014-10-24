package com.tkido.appcheck

object Processor {
  def apply(target:Target) :String = {
    val rank = Checker(target.appname, target.url)
    target.name + ":" + rank
  }
  
}