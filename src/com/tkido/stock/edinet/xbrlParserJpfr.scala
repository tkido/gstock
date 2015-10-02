package com.tkido.stock.edinet

object XbrlParserJpfr {
  import com.tkido.tools.Log
  import scala.xml._
  
  def apply(path :String) :Map[String, Long] = {
    Log d path
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
    
    if(Log.isDebug)
      for(node <- nodes)
        Log.log(node.prefix + "\t" + node.label + "\t" + node.text.toLong)
    
    nodes.toList.map(x => x.label -> x.text.toLong ).toMap
  }
}