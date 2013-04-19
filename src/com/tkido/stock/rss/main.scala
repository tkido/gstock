package com.tkido.stock.rss

object main extends App {
  import scala.io.Source
  import scala.collection.mutable.{Set => MSet}
  
  case class Stock(code:String, market:String){
  }
  
  def parseYahoo(code:String) :(String, String) = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=" + code
    val html = HtmlScraper(url)
    
    val rgexO = """<dt class="title">発行済株式数""".r
    var last = ""
    var target = ""
    for(line <- html){
      if(rgexO.findFirstIn(line).isDefined)
        target = last
      last = line
    }
    val rgexO2 = """<strong>([0-9,]+)</strong>""".r
    val mO = rgexO2.findFirstMatchIn(target)
    val outstanding = if(mO.isDefined) mO.get.group(1).dropRight(4) else "ERROR!!"
    
    val rgexM = """<dt>%s</dt>""".format(code).r
    var flag = false
    for(line <- html){
      if(flag){
    	target = line
    	flag = false
      }
      if(rgexM.findFirstIn(line).isDefined)
        flag = true
    }
    val marketString = target.replaceAll("<.*?>", "").trim
    println(marketString)
    val market = marketString match {
      case "東証" => "T"
      case "東証1部" => "T"
      case "東証2部" => "T"
      case "マザーズ" => "T"
      case "大証1部" => "OS"
      case "大証2部" => "OS"
      case "JQG" => "Q"
      case "JQS" => "Q"
      case _ => "X"
    }
    (market, outstanding)
  }
  
  def makeCodeList() :List[String] = {
    val s = Source.fromFile("data/rss/table.txt", "utf-8")
    val lines = try s.getLines.toList finally s.close
    val codes = lines.map(_.stripLineEnd)
    codes
  }
  
  def makeStockString(pair:Pair[String, Int]) :String = {
    val (code, n) = pair
    val (market, outstanding) = parseYahoo(code)
    
    val buf = new StringBuilder
    val s = "%s.%s".format(code, market)

    buf ++= code + "\t"
    buf ++= market + "\t"
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
    buf ++= outstanding 
    buf.toString()
  }
  
  val codeList = makeCodeList()
  val codeAndNum = codeList zip Range(2, codeList.size+2)
  val stockstrings = codeAndNum.map(makeStockString)
  val result = stockstrings.mkString("\n")
  println(result)
  

}