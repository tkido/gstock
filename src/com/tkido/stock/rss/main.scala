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
  
  def makeString(data:Map[String, String] ) :String = {
    val list = order.map(data(_))
    list.mkString("\t")
  }
  
  val startLine = 2
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

  val codeList = makeCodeList()
  val codeRowPairs = codeList zip Range(startLine, startLine+codeList.size)
  val datas = codeRowPairs.map(Scraping.makeData)
  for(data <- datas)
    ChartMaker.make(data("ID"), data("����"), data("���F"), data("����"))
  val strings = datas.map(makeString)
  val result = strings.mkString("\n")
  writeFile(result)
  println("OK!!")
}
