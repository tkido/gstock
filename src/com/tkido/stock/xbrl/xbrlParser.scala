package com.tkido.stock.xbrl

object XbrlParser {
  import scala.xml._
  import com.tkido.tools.Text
  
  val breakupData      = parseItems("data/xbrl/breakup_items.txt")
  val netCashData      = parseItems("data/xbrl/netcash_items.txt")
  val accrualsData     = parseItems("data/xbrl/accruals_items.txt")
  val freeCashFlowData = parseItems("data/xbrl/freecashflow_items.txt")
  
  def parseItems(path:String): Map[String, Int] = {
    def lineToPair(line:String) :Pair[String, Int] = {
      val arr = line.split("\t")
      Pair(arr(0), arr(1).toInt)
    }
    def isValid(line:String) :Boolean =
      line.nonEmpty && line.head != '#'
    val lines = Text.readLines(path)
    lines.filter(isValid).map(lineToPair).toMap
  }
  
  def parse(path :String) :Map[String, BigInt] = {
    val xml = XML.loadFile(path)
    val isConsolidated = (xml \\ "@id").exists(_.toString == "CurrentYearConsolidatedDuration")

    def isValid(node:Node) :Boolean = {
      def isValidPrefix :Boolean = {
        val rgex = """jpfr-t-[a-z]{3}""".r
        node.prefix match {
          case rgex() => true
          case _      => false
        }
      }
      def isValidContext:Boolean = {
        val contextRef = node.attribute("contextRef").get.text
        if(isConsolidated)
          contextRef match {
            case "CurrentYearConsolidatedDuration" => true
            case "CurrentYearConsolidatedInstant"  => true
            case _                                 => false
          }
        else
          contextRef match {
            case "CurrentYearNonConsolidatedDuration" => true
            case "CurrentYearNonConsolidatedInstant"  => true
            case _                                    => false
          }
      }
      node.text.nonEmpty &&
      isValidPrefix      &&
      isValidContext
    }
    val nodes = xml.child.filter(isValid)
        
    //for(node <- nodes) println(node.prefix + "\t" + node.label + "\t" + BigInt(node.text))
    nodes.toList.map(x => Pair(x.label, BigInt(x.text))).toMap
  }
}