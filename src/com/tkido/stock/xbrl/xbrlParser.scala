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
    lines.map(lineToPair).toMap
  }
  
  def parse(path :String) :Map[String, BigInt] = {
    def isValid(node:Node) :Boolean = {
      def isValidPrefix :Boolean = {
        val rgex = """jpfr-t-[a-z]{3}""".r
        node.prefix match {
          case rgex() => true
          case _ => false
        }
      }
      def isCurrentYearConsolidated:Boolean = {
        val contextRef = node.attribute("contextRef").get.text 
        contextRef == "CurrentYearConsolidatedDuration" ||
        contextRef == "CurrentYearConsolidatedInstant"
      }
      node.text.nonEmpty &&
      isValidPrefix &&
      isCurrentYearConsolidated
    }
    
    val xml = XML.loadFile(path)
    val nodes = xml.child.filter(isValid)
    
    //for(node <- nodes) println(node.prefix + "\t" + node.label + "\t" + BigInt(node.text))
    nodes.toList.map(x => Pair(x.label, BigInt(x.text))).toMap
  }
}