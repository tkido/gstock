package com.tkido.stock.rss

abstract class Company(code:String) {
  val data :Map[String, String]
  
  def toStringForExcel(row:Int) :String = {
    Company.order.map(x =>
      Company.reColumn.replaceAllIn(data(x), m => Company.columnsMap(m.group(1)) + row)
    ).mkString("\t")
  }
}
object Company{
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
                   "�]�A", "�]�P", "��", "��",
                   "��", "����", "�鉿", "�X�V")
  val abc = {
    for(char <- "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
      yield char.toString
    }.toList
  val columns = abc ::: abc.map("A"+_) ::: abc.map("B"+_)
  val columnsMap = order.zip(columns).toMap
  val reColumn = """�y(.*?)�z""".r
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) :Company = {
    code match {
      case reJp() => CompanyJp(code)
      case reUs() => CompanyUs(code)
    }
  }
}
