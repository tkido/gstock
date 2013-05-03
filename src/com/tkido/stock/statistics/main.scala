package com.tkido.stock.statistics

import scala.collection.mutable.{Map => MMap}

object main extends App {
  case class Deal(id:String, name:String, price:Int)

  object DealType extends Enumeration {
    val NEUTRAL, BUY, SELL = Value
  }

  val (namesmap, deals) = parseDeals()
  
  for(id <- namesmap.keys){
    println(id + "\t" + namesmap(id) + "\t" + sumDeals(id))
  }
  
  def sumDeals(target:String): Int = {
    val targets = deals.filter(_.id == target)
    targets.foldLeft(0)(_+_.price)
  }
  
  def parseDeals() :(MMap[String, String], List[Deal]) = {
    import scala.io.Source
    import DealType._ 
    
    val s = Source.fromFile("data/statistics/statistics.txt")
    val lines = try s.getLines.toList finally s.close
    
    val namesmap = MMap[String, String]()
    var deals = List[Deal]()
    for(line <- lines.tail){
      val array = line.split("\t")
      val name = array(1)
      val id = array(2)
      
      namesmap(id) = name
      
      val dealType = array(4) match {
        case "Š”Ž®Œ»•¨”ƒ" => BUY
        case "Š”Ž®Œ»•¨”„" => SELL
        case "”z“–"       => SELL
        case _            => NEUTRAL
      }
      val price = dealType match {
        case BUY     => -1 * array(13).toInt
        case SELL    => +1 * array(13).toInt
        case NEUTRAL => 0
      }
      deals = Deal(id, name, price) :: deals
    }
    (namesmap, deals)
  }

}