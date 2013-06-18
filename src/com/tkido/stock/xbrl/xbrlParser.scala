package com.tkido.stock.xbrl

object XbrlParser {
  import scala.xml._
  import scala.collection.mutable.{Map => MMap}
  
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
    
    for(li <- nodes) println(li.label + " = " + BigInt(li.text))
    
    val data = nodes.toList.map(x => Pair(x.label, BigInt(x.text))).toMap
    val breakupValue = data("CashAndDeposits") * 100 +
                       data("AccountsReceivableTrade") * 90
    println(breakupValue / 100)

  }
}