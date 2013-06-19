package com.tkido.stock.xbrl

object XbrlParser {
  import scala.xml._
  import scala.collection.mutable.{Map => MMap}
  import scala.io.Source
  
  val breakupData = parseItems("data/xbrl/breakup_items.txt")
  val netCashData = parseItems("data/xbrl/netcash_items.txt")
  val accrualsData = parseItems("data/xbrl/accruals_items.txt")
  
  def parseItems(path:String): Map[String, Int] = {
    def lineToPair(line:String) :Pair[String, Int] = {
      val arr = line.split("\t")
      Pair(arr(0), arr(1).toInt)
    }
    val s = Source.fromFile(path, "utf-8")
    val lines = try s.getLines.toList finally s.close
    val data = lines.map(lineToPair).toMap
    data
  }
  
  def parse(path :String) :Map[String, BigInt] = {
    println("parse start")
    val xml = XML.loadFile(path)
    
    def isValidPrefix(prefix: String): Boolean = {
      //println(prefix)
      val rgex = "jpfr-asr".r
      prefix match  {
        case "jpfr-t-cte" => true
        case "jpfr-t-cns" => true
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
    data
  }
}