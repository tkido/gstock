package com.tkido.stock.rss

object main extends App {
  import scala.io.Source
  import scala.collection.mutable.{Set => MSet}
  
  case class Stock(code:String, market:String){
  }
  
  def getTotal(code:String) :Option[String] = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=" + code
    val html = HtmlScraper(url)
    val p = """<dt class="title">発行済株式数""".r
    
    var last = ""
    var target = ""
    for(line <- html){
      if(p.findFirstIn(line).isDefined)
        target = last
      last = line
    }
    val p2 = """<strong>([0-9,]+)</strong>""".r
    val m = p2.findFirstMatchIn(target)
    Some(m.get.group(1).dropRight(4))
  }
  
  def makeStockList() :List[Stock] = {
    val s = Source.fromFile("data/rss/table.txt", "utf-8")
    val lines = try s.getLines.toList finally s.close
    
    var list = List[Stock]()
    for(line <- lines){
      val arr = line.split("\t")
      list = Stock(arr(0), arr(1)) :: list
    }
    list.reverse
  }
  
  def makeStockString(pair:Pair[Stock, Int]) :String = {
    val (stock, n) = pair
    val totalopt = getTotal(stock.code)
    if(totalopt.isEmpty) return "ERROR!!"
    val total = totalopt.get
    
    val buf = new StringBuilder
    val s = "%s.%s".format(stock.code, stock.market)

    buf ++= stock.code + "\t"
    buf ++= stock.market + "\t"
    buf ++= "=RSS|'%s'!銘柄名称\t".format(s) 
    buf ++= "=RSS|'%s'!現在値\t".format(s) 
    buf ++= "=RSS|'%s'!前日比率\t".format(s) 
    buf ++= "=RSS|'%s'!出来高/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!信用倍率\t".format(s) 
    buf ++= "=RSS|'%s'!信用買残/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!信用買残前週比/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!信用売残/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!信用売残前週比/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!年初来高値/D%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!年初来高値日付\t".format(s) 
    buf ++= "=RSS|'%s'!年初来安値/D%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!年初来安値日付\t".format(s) 
    buf ++= "=RSS|'%s'!市場部略称\t".format(s) 
    buf ++= "=RSS|'%s'!配当/D%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!ＰＥＲ\t".format(s) 
    buf ++= "=RSS|'%s'!ＰＢＲ\t".format(s) 
    buf ++= total 
    buf.toString()
  }
  
  val stock_list = makeStockList()
  val stock_and_num = stock_list zip Range(2, stock_list.size+2)
  val stockstrings = stock_and_num.map(makeStockString)
  val result = stockstrings.mkString("\n")
  println(result)
  

}