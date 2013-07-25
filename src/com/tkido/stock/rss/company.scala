package com.tkido.stock.rss

abstract class Company(code:String) {
  import com.tkido.stock.xbrl
  
  val data :Map[String, String]
  
  def toStringForExcel(row:Int) :String =
    Company.replaceColumn(data, row.toString).mkString("\t")
  
  def makeOtherData :Map[String, String] = {
    def getEnterpriseValue() :String =
      try{
        xbrl.Company(code).fairValue.toString
      }catch{
        case _ => ""
      }
    Map("ID"   -> code,
        "l"   -> """=IF(y»lz=" ", yOIz, y»lz)""",
        "¿" -> """=ylz*y­sz/100000""",
        "v"   -> """=IF(yPERz=0, 0, 1/yPERz""",
        "«"   -> """=IF(yvz=0, 0, yz/yvz""",
        "¦"   -> """=IF(yé¿z=0, 0, ylz/y¿z)""",
        "¿" -> """=IF(yé¿z="", 0, yé¿z/1000/y­sz)""",
        "XV" -> Logger.today,
        "é¿" -> getEnterpriseValue )
  }
    
}
object Company{
  val order = List("ID", "¼Ì", "l", 
                   "Å", "Å", "Å", "Å",
                   "»l", "OI", "Oä", "o",
                   "c", "cT·", "c", "cT·",
                   "N", "Nú", "NÀ", "NÀú",
                   "", "v", "«", "ROE", "©",
                   "PER", "PBR",
                   "Z", "DÒ", "ú",
                   "­s", "¿", "s", "ªÞ",
                   "ã\", "Ý§", "ãê", "ú",
                   "]A", "]P", "î", "û",
                   "¦", "¿", "é¿", "XV")
  val abc = {
    for(char <- "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
      yield char.toString
    }.toList
  val columns = abc ::: abc.map("A"+_) ::: abc.map("B"+_)
  val columnsMap = order.zip(columns).toMap
  val reColumn = """y(.*?)z""".r
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String) :Company = {
    code match {
      case reJp() => CompanyJp(code)
      case reUs() => CompanyUs(code)
    }
  }
  
  def replaceColumn(data:Map[String, String], row:String) :List[String] = {
    order.map(x =>
      reColumn.replaceAllIn(data(x), m => columnsMap(m.group(1)) + row)
    )
  }  
}
