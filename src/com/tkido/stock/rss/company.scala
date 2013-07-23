package com.tkido.stock.rss

abstract class Company(code:String) {
  val data :Map[String, String]
  
  def toStringForExcel(row:Int) :String = {
    def replaceAllColumns(source:String) :String = {
      var str = source
      Company.rgexColumn.findAllIn(source).matchData.foreach(
        m => str = str.replaceFirst(m.group(0), Company.columnNamesMap(m.group(1)) + row)
      )
      str
    }
    val list = Company.order.map(x => replaceAllColumns(data(x)))
    list.mkString("\t")
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
  val columnNames = abc ::: abc.map("A"+_) ::: abc.map("B"+_)
  val columnNamesMap = order.zip(columnNames).toMap
  val rgexColumn = """�y(.*?)�z""".r
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) :Company = {
    code match {
      case reJp() => CompanyJp(code)
      case reUs() => CompanyUs(code)
    }
  }
}
