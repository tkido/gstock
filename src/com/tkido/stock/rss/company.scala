package com.tkido.stock.rss

class Company(code:String, row:Int) {
  import com.tkido.stock.edinet
  import com.tkido.stock.log
  import com.tkido.stock.spider.Spider
  import com.tkido.stock.tdnet
  import com.tkido.tools.tryOrElse
  
  val data :Map[String, String] =
    Spider(code) ++
    tryOrElse(makeLogData _, Map()) ++
    tryOrElse(makeEdinetData _, Map()) ++
    tryOrElse(makeTdnetData _, Map()) ++
    Map("ID"   -> code,
        "値"   -> """=IF(【現値】=" ", 【前終】, 【現値】)""",
        "時価" -> """=【値】*【発行】/100000""",
        "益"   -> """=IF(【PER】=0, 0, 1/【PER】""",
        "性"   -> """=IF(【益】=0, 0, 【利】/【益】""",
        "率"   -> """=IF(【企価】=0, 0, 【値】/【株価】)""",
        "株価" -> """=IF(【企価】="", 0, 【企価】/1000/【発行】)""",
        "Vf"   -> """=(【年高】-1)/【Vol】""",
        "更新" -> Company.today)
  
  override def toString =
    Company.replaceColumn(data, row.toString).mkString("\t")
  
  def makeLogData :Map[String, String] = {
    Map("log" -> log.Reporter(code))
  }
  
  def makeEdinetData :Map[String, String] = {
    val eCompany = edinet.Company(code)
    Map("企価"   -> eCompany.fairValue.toString,
        "edinet" -> eCompany.toString)
  }
  
  def makeTdnetData :Map[String, String] = {
    val tCompany = tdnet.Company(code)
    Map("tdnet" -> tCompany.toString)
  }
  
}
object Company{
  import java.util.Date
  import com.tkido.tools.Logger
  
  def apply(code:String, row:Int) :Company = {
    Logger.info(code)
    new Company(code, row)
  }
  val today = "%tY/%<tm/%<td".format(new Date)
  
  val order = List("ID", "名称", "R", "値", 
                   "最売", "最売数", "最買", "最買数",
                   "現値", "前終", "前比",
                   "出来", "日出", "週出", "月出",
                   "買残", "買残週差", "売残", "売残週差",
                   "年高", "年高日", "年安", "年安日",
                   "Vol", "Vf", "SPR", "RCI",
                   "利", "益", "性", "ROE", "自",
                   "PER", "PBR", "市", "時価",
                   "決算", "優待", "落日", "発行", "分類",
                   "代表", "設立", "上場", "決期",
                   "従連", "従単", "齢", "収",
                   "率", "株価", "企価", "更新" )
  
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
