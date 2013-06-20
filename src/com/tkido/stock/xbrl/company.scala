package com.tkido.stock.xbrl

class Company(code:String) {
  import java.io.File
  
  val files = XbrlFinder.find(code)
  val reports = files.map(Report(_))
  
  override def toString = {
    val buf = new StringBuilder
    buf ++= "コード：%s\n".format(code)
    for(report <- reports){
      buf ++= "解散価値:%s\n".format(report.breakupValue)
      buf ++= "ネットキャッシュ:%s\n".format(report.netCash)
      buf ++= "アクルーアル:%s\n".format(report.accruals)
    }
    buf.toString
  }
}

object Company{
  def apply(code:String) = new Company(code)
}