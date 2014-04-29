package com.tkido.stock.tdnet

object XbrlParserInline {
  import com.tkido.tools.Logger
  import com.tkido.tools.xml._
  import java.io.File
  import scala.xml._
  
  def apply(path :String) :Report[Long] = {
    val fileName = new File(path).getName
    val isQuarter = fileName.charAt(4) == 'q'
    val isConsolidated = fileName.charAt(5) == 'c'
    
    val xml = XML.loadFile(path)
    val nodeSeq = (xml \\ "_")
    
    val dateJp = nodeSeq.filter(_ \ "@name" exists(_.text == "tse-ed-t:FilingDate"))(0).text
    val date = com.tkido.tools.Date.fromJpToSimple(dateJp)
    
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
    
    val order = List("NetSales", "OperatingIncome", "OrdinaryIncome", "NetIncome")
    def isValid(node:Node) :Boolean = {
      def isValidName    = node.attribute("name").nonEmpty &&
                           order.contains(node.attribute("name").get.text.replaceFirst("tse-ed-t:", ""))
      def isValidLabel   = (node.label == "nonFraction")
      def isValidPrefix  = (node.prefix == "ix")
      def isValidContext = (context == node.attribute("contextRef").get.text)
      isValidName && isValidLabel && isValidPrefix && isValidContext
    }
    val nodes = nodeSeq.filter(isValid)
    
    def nodeToNumber(node:Node) :Long = {
      val literal = node.text.replaceAll(",", "")
      val scale = node.attribute("scale").get.text.toInt
      (literal + "0" * scale).toLong
    }
    val map = nodes.toList.map(n =>
      n.attribute("name").get.text.replaceFirst("tse-ed-t:", "") -> nodeToNumber(n)
    ).toMap
    
    val data = order.map(map(_))
    
    Report(year, quarter, date, month, data)
  }
}