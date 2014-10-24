package com.tkido.appcheck

object Checker {
  import scala.xml._
  
  def apply(apptitle:String, url:String) :String ={
    val xml = XML.load(url)
    val entries = xml \ "entry"
    val pairs = entries zip Range(1, entries.size+1)
    
    for (pair <- pairs) {
      val (entry, rank) = pair
      val name = (entry \ "name")(0).text
      if(name == apptitle){
        return rank.toString
      }
    }
    "-"
  }
}