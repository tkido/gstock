package com.tkido.stock.tdnet

object XbrlParser {
  import com.tkido.tools.Logger
  import java.io.File
  import scala.xml._
  
  def apply(path :String) :Map[String, BigInt] = {
    Logger.debug(path)
    
    val fileName = new File(path).getName
    val isQuarter = fileName.charAt(6) == 'q'
    val isConsolidated = fileName.charAt(7) == 'c'
    
    Logger.debug("fileName = %s" format fileName)
    Logger.debug("isQuarter = %s" format isQuarter)
    Logger.debug("isConsolidated = %s" format isConsolidated)
    
    val xml = XML.loadFile(path)
    
    val quater =
      if(!isQuarter) 4
      else (xml \ "QuarterlyPeriod")(0).text.toInt
    Logger.debug("quater = %s" format quater)
    
    val isOldType =
      if(!isQuarter) false
      else if(isConsolidated) (xml \\ "@id").exists(_.toString == "CurrentQuarterConsolidatedDuration")
      else (xml \\ "@id").exists(_.toString == "CurrentQuarterNonConsolidatedDuration")
    Logger.debug("isOldType = %s" format isOldType)
    
    val context = (quater, isConsolidated, isOldType) match {
        case (4, true , _)      => "CurrentYearConsolidatedDuration"
        case (4, false, _)      => "CurrentYearNonConsolidatedDuration"
        case (_, true , true)   => "CurrentQuarterConsolidatedDuration"
        case (_, false, true)   => "CurrentQuarterNonConsolidatedDuration"
        case (x, true , false)  => "CurrentAccumulatedQ%sConsolidatedDuration" format x
        case (x, false, false)  => "CurrentAccumulatedQ%sNonConsolidatedDuration" format x
      }
    Logger.debug("context = %s" format context)
    
    val year =
      xml.child.find(n =>
        n.label == "context" && n.attribute("id").get.text == context
      ).get.\("period").\("startDate").text.take(4).toInt
    Logger.debug("year = %s" format year)

    def isValid(node:Node) :Boolean = {
      def isValidLabel =
        node.label match {
          case "NetSales" => true
          case "OperatingIncome" => true
          case "OrdinaryIncome" => true
          case "NetIncome" => true
          case _ => false
        }
      def isValidPrefix =
        node.prefix == "tse-t-ed"
      def isValidContext =
        context == node.attribute("contextRef").get.text
      isValidLabel && isValidPrefix && isValidContext
    }
    val nodes = xml.child.filter(isValid)
    
    if(Logger.isDebug)
      for(node <- nodes)
        Logger.log(node.prefix + "\t" + node.label + "\t" + node.text)
    
    nodes.toList.map(n => n.label -> BigInt(n.text) ).toMap
  }
}