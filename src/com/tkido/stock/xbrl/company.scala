package com.tkido.stock.xbrl

class Company(code:String) {
  import java.io.File
  
  val files = XbrlFinder.find(code)
  val reports = files.map(Report(_))
  
  override def toString = {
    val buf = new StringBuilder
    buf ++= "�R�[�h�F%s\n".format(code)
    for(report <- reports){
      buf ++= "���U���l:%s\n".format(report.breakupValue)
      buf ++= "�l�b�g�L���b�V��:%s\n".format(report.netCash)
      buf ++= "�A�N���[�A��:%s\n".format(report.accruals)
    }
    buf.toString
  }
}

object Company{
  def apply(code:String) = new Company(code)
}