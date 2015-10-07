package com.tkido.stock.edinet

import com.tkido.tools.Log
import scala.xml._

object XbrlParserJpcrp {
  def apply(path :String) :Map[String, Long] = {
    Log d path
    val xml = XML.loadFile(path)
    
    def isNetIncome(node:Node) :Boolean = {
      def isValidPrefix :Boolean = {
        val rgex = """jppfs_cor""".r
        node.prefix match {
          case rgex() => true
          case _      => false
        }
      }
      def isValidContext:Boolean = {
        val contextRef = node.attribute("contextRef").get.text
        contextRef match {
          case "CurrentYearDuration" => true
          case _                     => false
        }
      }
      node.text.nonEmpty && isValidPrefix && isValidContext && node.label == "NetIncome"
    }
    val isConsolidated = xml.child.exists(isNetIncome)
    if(Log.isDebug)
      Log.log("isConsolidated = %s".format(isConsolidated))
    
    
    def isValid(node:Node) :Boolean = {
      def isValidPrefix :Boolean = {
        val rgex = """jppfs_cor""".r
        node.prefix match {
          case rgex() => true
          case _      => false
        }
      }
      def isValidContext:Boolean = {
        val contextRef = node.attribute("contextRef").get.text
        if(isConsolidated){
          contextRef match {
            case "CurrentYearInstant"  => true
            case "CurrentYearDuration" => true
            case _                     => false
          }
        }else{
          contextRef match {
            case "CurrentYearInstant_NonConsolidatedMember"  => true
            case "CurrentYearDuration_NonConsolidatedMember" => true
            case _                                           => false
          }
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