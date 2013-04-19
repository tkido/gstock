package com.tkido.stock.rss

object main extends App {
  import scala.io.Source
  import scala.collection.mutable.{Set => MSet}
  
  case class Stock(code:String, market:String){
  }
  
  def parseYahoo(code:String) :(String, String) = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=" + code
    val html = HtmlScraper(url)
    
    val rgexO = """<dt class="title">���s�ϊ�����""".r
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
      case "����" => "T"
      case "����1��" => "T"
      case "����2��" => "T"
      case "�}�U�[�Y" => "T"
      case "���1��" => "OS"
      case "���2��" => "OS"
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
    buf ++= outstanding 
    buf.toString()
  }
  
  val codeList = makeCodeList()
  val codeAndNum = codeList zip Range(2, codeList.size+2)
  val stockstrings = codeAndNum.map(makeStockString)
  val result = stockstrings.mkString("\n")
  println(result)
  

}