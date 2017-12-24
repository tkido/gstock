package com.tkido.stock.tdnet

import com.tkido.tools.Date.fromJpToSimple
import com.tkido.tools.Log
import com.tkido.tools.OpsNum
import java.io.File
import scala.xml._

object XbrlParserInline {
  def apply(path :String) :Report[Long] = {
    val fileName = new File(path).getName
    Log i fileName
    val isQuarter = fileName.charAt(4) == 'q'
    val isConsolidated = fileName.charAt(5) == 'c'
    
    val xml = XML.loadFile(path)
    val nodeSeq = (xml \\ "_")
    
    val dateJp = nodeSeq.filter(_ \ "@name" exists(_.text == "tse-ed-t:FilingDate"))(0).text
    val date = fromJpToSimple(dateJp)
    
    val quarter =
      if(!isQuarter) 4
      else nodeSeq.filter(_ \ "@name" exists(_.text == "tse-ed-t:QuarterlyPeriod"))(0).text.toInt
    val context = (quarter, isConsolidated) match {
        case (4, true)  => "CurrentYearDuration_ConsolidatedMember_ResultMember"
        case (4, false) => "CurrentYearDuration_NonConsolidatedMember_ResultMember"
        case (x, true)  => "CurrentAccumulatedQ%sDuration_ConsolidatedMember_ResultMember" format x
        case (x, false) => "CurrentAccumulatedQ%sDuration_NonConsolidatedMember_ResultMember" format x
      }
    
    val period =
      nodeSeq.find(n =>
        n.label == "context" && n.attribute("id").get.text == context
      ).get.\("period")
    val year = period.\("startDate").text.take(4).toInt
    val month = period.\("endDate").text.take(7)
    
    val order = List("NetSales", "OperatingRevenues",
                     "OperatingIncome",
                     "OrdinaryIncome",
                     "NetIncome", "ProfitAttributableToOwnersOfParent")
    def isValid(node:Node) :Boolean = {
      (node.attribute("name") match{
        case None => false
        case Some(nodeSeq) => order.contains(nodeSeq.text.replaceFirst("tse-ed-t:", ""))
      }) &&
      (node.label == "nonFraction") &&
      (node.prefix == "ix") &&
      (node.attribute("contextRef").get.text == context) &&
      node.text.isNumeric()
    }
    val nodes = nodeSeq.filter(isValid)
    
    def nodeToNumber(node:Node) :Long = {
      val literal = node.text.replaceAll(",", "")
      val scale = node.attribute("scale").get.text.toInt
      val sign = if(node.attribute("sign").nonEmpty) -1 else 1
      sign * (literal + "0" * scale).toLong
    }
    val dataMap = nodes.toList.map(n =>
      n.attribute("name").get.text.replaceFirst("tse-ed-t:", "") -> nodeToNumber(n)
    ).toMap
    
    val data = order.collect{case s if dataMap.contains(s) => dataMap(s)} //choose between "NetIncome" and "ProfitAttributableToOwnersOfParent"
    
    Report(year, quarter, date, month, data)
  }
}