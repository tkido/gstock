package com.tkido.stock.xbrl

object XbrlParser {
  import scala.xml._
  
  def parse(path :String){
    println("parse start")
    val xml = XML.loadFile(path)
    
    
    def isValidPrefix(prefix: String): Boolean = {
      //println(prefix)
      val rgex = "jpfr-asr".r
      prefix match  {
        case "jpfr-t-cte" => true
        case rgex() => true
        case _ => false
      }
    }

    def isCurrentYearConsolidated(node: Node): Boolean = {
      node.text.nonEmpty &&
      isValidPrefix(node.prefix) &&
      (node.attribute("contextRef").get.text == "CurrentYearConsolidatedDuration" ||
       node.attribute("contextRef").get.text == "CurrentYearConsolidatedInstant")
      
    }
    
    val nodes = xml.child.filter(isCurrentYearConsolidated)
    for(li <- nodes)
      println(li.label + " = " + li.text)

  }
}