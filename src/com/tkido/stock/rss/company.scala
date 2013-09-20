package com.tkido.stock.rss

abstract class Company(code:String, row:Int) {
  import com.tkido.stock.xbrl
  
  val data :Map[String, String]
  
  override def toString =
    Company.replaceColumn(data, row.toString).mkString("\t")
  
  def makeOtherData :Map[String, String] = {
    makeXbrlData ++
    Map("ID"   -> code,
        "値"   -> """=IF(【現値】=" ", 【前終】, 【現値】)""",
        "時価" -> """=【値】*【発行】/100000""",
        "益"   -> """=IF(【PER】=0, 0, 1/【PER】""",
        "性"   -> """=IF(【益】=0, 0, 【利】/【益】""",
        "率"   -> """=IF(【企価】=0, 0, 【値】/【株価】)""",
        "株価" -> """=IF(【企価】="", 0, 【企価】/1000/【発行】)""",
        "更新" -> Company.today)
  }
  
  def makeXbrlData :Map[String, String] = {
    try{
      val xCompany = xbrl.Company(code)
      Map("企価" -> xCompany.fairValue.toString,
          "表"   -> xCompany.toString)
    }catch{
      case _ => Map()
    }
  }
  
}
object Company{
  import java.util.Date
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String, row:Int) :Company = {
    code match {
      case reJp() => CompanyJp(code, row)
      case reUs() => CompanyUs(code, row)
    }
  }
  
  val today = "%tY/%<tm/%<td".format(new Date)
  
  val order = List("ID", "名称", "R", "値", 
                   "最売", "最売数", "最買", "最買数",
                   "現値", "前終", "前比", "出来",
                   "買残", "買残週差", "売残", "売残週差",
                   "年高", "年高日", "年安", "年安日",
                   "利", "益", "性", "ROE", "自",
                   "PER", "PBR",
                   "決算", "優待", "落日",
                   "発行", "時価", "市", "分類",
                   "代表", "設立", "上場", "決期",
                   "従連", "従単", "齢", "収",
                   "率", "株価", "企価", "更新")
  
  val abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".map(_.toString).toList
  val columns = abc ::: abc.map("A"+_) ::: abc.map("B"+_)
  val columnsMap = (order zip columns).toMap
  val reColumn = """【(.*?)】""".r
  
  def replaceColumn(data:Map[String, String], row:String) :List[String] = {
    order.map(x =>
      reColumn.replaceAllIn(data.getOrElse(x, "-"), m => columnsMap(m.group(1)) + row)
    )
  }
}
