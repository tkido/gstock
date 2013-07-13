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
    
    ChartMaker.make(data("ID"), data("����"), data("���F"), data("����"))
    
    val order = List("ID", "����", "�l", 
                     "�Ŕ�", "�Ŕ���", "�Ŕ�", "�Ŕ���",
                     "���l", "�O�I", "�O��", "�o��",
                     "���c", "���c�T��", "���c", "���c�T��",
                     "�N��", "�N����", "�N��", "�N����",
                     "��", "�v", "��", "ROE", "��",
                     "PER", "PBR",
                     "���Z", "�D��", "����",
                     "���s", "����", "�s", "����",
                     "��\", "�ݗ�", "���", "����",
                     "�]�A", "�]�P", "��", "��")
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
