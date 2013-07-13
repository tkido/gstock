package com.tkido.stock.rss

object main extends App {
  import scala.io.Source
  import java.io.PrintWriter
  
  def makeCodeList() :List[String] = {
    val s = Source.fromFile("data/rss/table.txt", "utf-8")
    val lines = try s.getLines.toList finally s.close
    val codes = lines.map(_.stripLineEnd)
    codes
  }
  
  def writeFile(data: String) {
    val out = new PrintWriter("data/rss/result.txt")
    out.println(data)
    out.close
  }
  
  def makeString(pair:Pair[String, Int]) :String = {
    val data = Scraping.makeData(pair)
    
    ChartMaker.make(data("ID"), data("名称"), data("特色"), data("事業"))
    
    val order = List("ID", "名称", "値", 
                     "最売", "最売数", "最買", "最買数",
                     "現値", "前終", "前比", "出来",
                     "買残", "買残週差", "売残", "売残週差",
                     "年高", "年高日", "年安", "年安日",
                     "利", "益", "性", "ROE", "自",
                     "PER", "PBR",
                     "決算", "優待", "落日",
                     "発行", "時価", "市", "分類",
                     "代表", "設立", "上場", "決期",
                     "従連", "従単", "齢", "収")
    val list = order.map(data(_))
    list.mkString("\t")
  }
  
  val startLine = 2
  val codeList = makeCodeList()
  val codeRowPair = codeList zip Range(startLine, codeList.size+startLine)
  val strings = codeRowPair.map(makeString)
  val result = strings.mkString("\n")
  writeFile(result)
  println("OK!!")
}
