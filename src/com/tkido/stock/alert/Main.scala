package com.tkido.stock.alert

object main extends App {
  import scala.io.Source
  import scala.collection.mutable.{Set => MSet}
  
  case class Alert(code:String, name:String, floor:Option[Int], ceil:Option[Int], comment:Option[String]){
    def isDefined = floor.isDefined || ceil.isDefined
    def isEmpty = floor.isEmpty && ceil.isEmpty
  }
  
  def getJpPrice(code:String) :Option[Int] = {
    val url = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=" + code
    val html = HtmlScraper(url)
    val p = """<td class="stoksPrice">([0-9,]+)</td>""".r
    html.collectFirst{ case p(m) => m.filter(_ != ',').toInt }
  }
  
  def getUsPrice(code:String) :Option[Int] = {
    val url = "http://finance.yahoo.com/q?s=" + code
    val html = HtmlScraper(url)
    val p = """<span class="time_rtq_ticker"><span id="yfs_l84_([a-z]+)">([0-9.]+)</span>""".r
    for(line <- html){
      val m = p.findFirstMatchIn(line)
      if(m.isDefined) return Some(m.get.group(2).filter(_ != '.').toInt)
    }
    None
  }
  
  def getPrice(code:String) :Option[Int] = {
    try{
      getJpPrice(code.toInt.toString)
    }catch{
      case _ => getUsPrice(code)
    }
  }
  
  def makeAlertList() :List[Alert] = {
    val s = Source.fromFile("data/alert/table.txt", "utf-8")
    val lines = try s.getLines.toList finally s.close
    
    var list = List[Alert]()
    for(line <- lines){
      val arr = line.split("\t")
      
      val floor = try{
        Some(arr(2).filter(_ != '.').toInt)
      }catch{
        case _ => None
      }
      val ceil = try{
        Some(arr(3).filter(_ != '.').toInt)
      }catch{
        case _ => None
      }
      val comment = try{
        Some(arr(4))
      }catch{
        case _ => None
      }
      
      list = Alert(arr(0), arr(1), floor, ceil, comment) :: list
    }
    list.reverse
  }
  
  def makeAlertString(alert:Alert) :String = {
    if(alert.isEmpty) return ""
    val priceopt = getPrice(alert.code)
    if(priceopt.isEmpty) return "I couldn't get %s's price".format(alert.code)
    val price = priceopt.get
    
    val buf = new StringBuilder
    if(alert.floor.isDefined)
      if(price < alert.floor.get)
        buf ++= "%s's price is %d now. It is under %d !!\n".format(alert.code, price, alert.floor.get)
    if(alert.ceil.isDefined)
      if(price > alert.ceil.get)
        buf ++= "%s's price is %d now. It is over %d !!\n".format(alert.code, price, alert.ceil.get)
    buf.toString()
  }
  
  def getMailBase :String = {
    val s = Source.fromFile("data/mailbase.txt", "utf-8")
    val lines = try s.getLines.toList finally s.close
    lines.mkString("\n") + "\n\n"
  }
  
  def printMail(body:String) {
    import java.io.PrintWriter
    val out = new PrintWriter("mail.txt")
    if(result.size > 0) 
      out.println(getMailBase + body)
    else
      out.println("")
    out.close
  }
  
  val alertlist = makeAlertList()
  val alertstrings = alertlist.map(makeAlertString)
  val result = alertstrings.mkString
  printMail(result)
  

}