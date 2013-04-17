package com.tkido.stock.rss

object main extends App {
  import scala.io.Source
  import scala.collection.mutable.{Set => MSet}
  
  case class Stock(code:String, market:String){
  }
  
  def getTotal(code:String) :Option[String] = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=" + code
    val html = HtmlScraper(url)
    val p = """<dt class="title">���s�ϊ�����""".r
    
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
    buf ++= "=RSS|'%s'!��������\t".format(s) 
    buf ++= "=RSS|'%s'!���ݒl\t".format(s) 
    buf ++= "=RSS|'%s'!�O���䗦\t".format(s) 
    buf ++= "=RSS|'%s'!�o����/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!�M�p�{��\t".format(s) 
    buf ++= "=RSS|'%s'!�M�p���c/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!�M�p���c�O�T��/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!�M�p���c/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!�M�p���c�O�T��/T%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!�N�������l/D%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!�N�������l���t\t".format(s) 
    buf ++= "=RSS|'%s'!�N�������l/D%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!�N�������l���t\t".format(s) 
    buf ++= "=RSS|'%s'!�s�ꕔ����\t".format(s) 
    buf ++= "=RSS|'%s'!�z��/D%d\t".format(s, n) 
    buf ++= "=RSS|'%s'!�o�d�q\t".format(s) 
    buf ++= "=RSS|'%s'!�o�a�q\t".format(s) 
    buf ++= total 
    buf.toString()
  }
  
  val stock_list = makeStockList()
  val stock_and_num = stock_list zip Range(2, stock_list.size+2)
  val stockstrings = stock_and_num.map(makeStockString)
  val result = stockstrings.mkString("\n")
  println(result)
  

}