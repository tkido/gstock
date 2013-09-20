package com.tkido.stock.xbrl

object XbrlParser {
  import com.tkido.tools.Logger
  import scala.xml._
  
  def apply(path :String) :Map[String, BigInt] = {
    Logger.debug(path)
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
      node.text.nonEmpty && isValidPrefix && isValidContext
    }
    val nodes = xml.child.filter(isValid)
    
    if(Logger.isDebug)
      for(node <- nodes)
        Logger.log(node.prefix + "\t" + node.label + "\t" + BigInt(node.text))
    
    nodes.toList.map(x => x.label -> BigInt(x.text) ).toMap
  }
}