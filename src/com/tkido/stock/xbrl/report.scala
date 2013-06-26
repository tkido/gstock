package com.tkido.stock.xbrl

class Report(path:String) {
  import java.io.File
  val year = new File(path).getName.slice(20, 24).toInt
  val data = XbrlParser.parse(path)
  
  def breakupValue = sumItems(XbrlParser.breakupData)
  def netCash      = sumItems(XbrlParser.netCashData)
  def accruals     = sumItems(XbrlParser.accrualsData)
  def freeCashFlow = sumItems(XbrlParser.freeCashFlowData)
  
  def netIncome = data("NetIncome")
  
  def sumItems(items:Map[String, Int]) :BigInt = {
    var sum = BigInt(0)
    for((key, value) <- items)
      if(data.contains(key))
        sum += data(key) * value 
    sum / 100
  }
  
  override def toString = {
    val buf = new StringBuilder
    buf ++= "�N�x\t%s\n".format(year)
    buf ++= "���U���l\t%s\n".format(breakupValue)
    buf ++= "�l�b�g�L���b�V��\t%s\n".format(netCash)
    buf ++= "�A�N���[�A��\t%s\n".format(accruals)
    buf ++= "�����v\t%s\n".format(netIncome)
    buf ++= "�t���[�L���b�V���t���[\t%s\n".format(freeCashFlow)
    buf.toString
  }
}

object Report{
  def apply(path:String) = new Report(path)
}