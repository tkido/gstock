package com.tkido.stock.statistics

import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.{Set => MSet}

object main extends App {
  case class Stock(id:String, name:String, value:String, delta:String, group:String)  
  case class Deal(id:String, name:String, price:Int)

  object DealType extends Enumeration {
    val NEUTRAL, BUY, SELL = Value
  }

  val (stocksmap, stocks) = makeStocks()
  val (namesmap, deals) = parseDeals()
  
  for(id <- namesmap.keys){
    val value = if (stocksmap.contains(id))
      stocksmap(id).value.filter(_ != ',').toInt
      else 0
    val price = sumDeals(id) + value
    println(id + "\t" + namesmap(id) + "\t" + price)
  }
  
  def sumDeals(target:String): Int = {
    val targets = deals.filter(_.id == target)
    val sum = targets.foldLeft(0)(_+_.price)
    sum
  }

  def makeStocks() :(MMap[String, Stock], MSet[Stock]) = {
    import scala.io.Source
    val s = Source.fromFile("data/statistics/jp.txt")
    val lines = try s.getLines.toList finally s.close
    
    val stocks = MSet[Stock]()
    val stocksmap = MMap[String, Stock]()
    for(line <- lines){
      val array = line.split("\t")
      val idname = array(1).split(" ")
      val id = idname(0)
      val stock = Stock(id, idname(1), array(9), array(7), "")
      
      stocks += stock
      stocksmap(id) = stock
    }
    (stocksmap, stocks)
  }
  
  def parseDeals() :(MMap[String, String], MSet[Deal]) = {
    import scala.io.Source
    val s = Source.fromFile("data/statistics/statistics.txt")
    val lines = try s.getLines.toList finally s.close
    
    val namesmap = MMap[String, String]()
    val deals = MSet[Deal]()
    for(line <- lines.tail){
      val array = line.split("\t")
      val name = array(1)
      val id = array(2)
      
      namesmap(id) = name
      
      val dealType = array(4) match {
        case "Š”Ž®Œ»•¨”ƒ" => DealType.BUY
        case "Š”Ž®Œ»•¨”„" => DealType.SELL
        case "”z“–"       => DealType.SELL
        case _            => DealType.NEUTRAL
      }
      
      val coefficient = dealType match {
        case DealType.BUY     => -1
        case DealType.SELL    => +1
        case DealType.NEUTRAL => 0
      }
      val price = coefficient * array(13).toInt
      deals += Deal(id, name, price)
    }
    (namesmap, deals)
  }

}