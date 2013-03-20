package com.tkido.stock.grouping

case class Stock(id:String, name:String, value:String, delta:String, group:String)

import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.{Set => MSet}

object main extends App {
  val groupmap = makeGroupMap()
  val stocks_jp = makeJPStocks()
  val stocks_us = makeUSStocks()
  val stocks = (stocks_jp | stocks_us).toList.sortBy(- _.value.filter(_ != ',').toInt)
  printStocks(stocks)
  
  def makeGroupMap() :MMap[String, String] = {
    import scala.io.Source
    val s = Source.fromFile("data/grouping/table.txt")
    val lines = try s.getLines.toList finally s.close
    
    val map = MMap[String, String]()
    for(line <- lines){
      val array = line.split("\t")
      map(array(0)) = array(2)
    }
    map
  }
  
  def makeJPStocks() :MSet[Stock] = {
    import scala.io.Source
    val s = Source.fromFile("data/grouping/jp.txt")
    val lines = try s.getLines.toList finally s.close
    
    val stocks = MSet[Stock]()
    for(line <- lines){
      val array = line.split("\t")
      val idname = array(1).split(" ")
      val id = idname(0)
      stocks += Stock(id, idname(1), array(9), array(7), groupmap(id))
    }
    stocks
  }
  
  def makeUSStocks() :MSet[Stock] = {
    import scala.io.Source
    val s = Source.fromFile("data/grouping/us.txt")
    val lines = try s.getLines.toList finally s.close
    
    val stocks = MSet[Stock]()
    val num = lines.size / 4
    for(i <- Range(0, num)){
      val name = lines(0+i*4).trim
      val id = lines(1+i*4).split(" ")(0)
      val value = lines(3+i*4).stripSuffix("‰~")
      stocks += Stock(id, name, value, "", groupmap(id))
    }
    stocks
  }
  
  def printStocks(stocks:List[Stock]) {
    val buf = new StringBuilder
    for(i <- Range(1, 13)){
      val groups = stocks.filter(_.group == i.toString)
      for(stock <- groups)
        buf ++= stock.id + " " + stock.name + "\t"
      buf ++= "\n"
      for(stock <- groups)
        buf ++= stock.value + "\t"
      buf ++= "\n"
    }
    println(buf.toString)
  }
  
}