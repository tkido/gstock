package com.tkido.stock.rss

object main extends App {
  import scala.io.Source
  import java.io.PrintWriter
  
  def makeCodes :List[String] = {
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
  
  val codes = makeCodes
  val companies = codes.map(Company(_))
  
  val offset = 2  
  val strings = for((company, index) <- companies.zipWithIndex) yield company.toStringForExcel(index+offset)
  
  val result = strings.mkString("\n")
  writeFile(result)
  
  println("OK!!")
}
