package com.tkido.appcheck

object Checker {
  import scala.xml._
  
  def apply(target:Target) :Result ={
    val xml = XML.load(target.url)
    
    val updated = (xml \ "updated")(0).text
    
    val entries = xml \ "entry"
    val pairs = entries zip Range(1, entries.size+1)
    
    for (pair <- pairs) {
      val (entry, rank) = pair
      val name = (entry \ "name")(0).text
      if(name == target.appname){
        return Result(rank.toString, updated)
      }
    }
    Result("-", updated)
  }
}