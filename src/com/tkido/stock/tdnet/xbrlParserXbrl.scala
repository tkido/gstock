package com.tkido.stock.tdnet

import com.tkido.tools.Log
import java.io.File
import scala.xml._

object XbrlParserXbrl {
  def apply(path :String) :Report[Long] = {
    val fileName = new File(path).getName
    val isQuarter = fileName.charAt(6) == 'q'
    val isConsolidated = fileName.charAt(7) == 'c'
    
    val xml = XML.loadFile(path)
    
    val date = (xml \ "FilingDate")(0).text
    val quarter =
      if(!isQuarter) 4
      else (xml \ "QuarterlyPeriod")(0).text.toInt
    val isOldType =
      if(!isQuarter) false
      else if(isConsolidated) (xml \\ "@id").exists(_.toString == "CurrentQuarterConsolidatedDuration")
      else (xml \\ "@id").exists(_.toString == "CurrentQuarterNonConsolidatedDuration")
    val context = (quarter, isConsolidated, isOldType) match {
        case (4, true , _)      => "CurrentYearConsolidatedDuration"
        case (4, false, _)      => "CurrentYearNonConsolidatedDuration"
        case (_, true , true)   => "CurrentQuarterConsolidatedDuration"
        case (_, false, true)   => "CurrentQuarterNonConsolidatedDuration"
        case (x, true , false)  => "CurrentAccumulatedQ%sConsolidatedDuration" format x
        case (x, false, false)  => "CurrentAccumulatedQ%sNonConsolidatedDuration" format x
      }
    val period =
      xml.child.find(n =>
        n.label == "context" && n.attribute("id").get.text == context
      ).get.\("period")
    val year = period.\("startDate").text.take(4).toInt
    val month = period.\("endDate").text.take(7)
    
    val order = List("NetSales", "OperatingIncome", "OrdinaryIncome", "NetIncome")
    def isValid(node:Node) :Boolean = {
      def isValidLabel   = order.contains(node.label)
      def isValidPrefix  = node.prefix == "tse-t-ed"
      def isValidContext = context == node.attribute("contextRef").get.text
      isValidLabel && isValidPrefix && isValidContext
    }
    val nodes = xml.child.filter(isValid)
    
    val map = nodes.toList.map(n => n.label -> n.text.toLong ).toMap
    val data = order.map(map(_))
    Report(year, quarter, date, month, data)
  }
}