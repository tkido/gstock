package com.tkido.stock.rss

abstract class Company(code:String) {
  println("Company:%s".format(code))
  val data :Map[String, String]
  
  def toStringForExcel(row:Int) :String = {
    val list = Company.order.map(data(_).replaceAll("%d", row.toString))
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
                   "�]�A", "�]�P", "��", "��")  
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) :Company = {
    code match {
      case reJp() => CompanyJp(code)
      case reUs() => CompanyUs(code)
    }
  }
}
