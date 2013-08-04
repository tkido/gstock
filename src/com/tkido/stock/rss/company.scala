package com.tkido.stock.rss

abstract class Company(code:String, row:Int) {
  import com.tkido.stock.xbrl
  println(code)

  val data :Map[String, String]
  
  def toStringForExcel :String =
    Company.replaceColumn(data, row.toString).mkString("\t")
  
  def makeOtherData :Map[String, String] = {
    def getEnterpriseValue() :String =
      try{
        xbrl.Company(code).fairValue.toString
      }catch{
        case _ => ""
      }
    Map("ID"   -> code,
        "’l"   -> """=IF(yŒ»’lz=" ", y‘OIz, yŒ»’lz)""",
        "‰¿" -> """=y’lz*y”­sz/100000""",
        "‰v"   -> """=IF(yPERz=0, 0, 1/yPERz""",
        "«"   -> """=IF(y‰vz=0, 0, y—˜z/y‰vz""",
        "—¦"   -> """=IF(yŠé‰¿z=0, 0, y’lz/yŠ”‰¿z)""",
        "Š”‰¿" -> """=IF(yŠé‰¿z="", 0, yŠé‰¿z/1000/y”­sz)""",
        "XV" -> Logger.today,
        "Šé‰¿" -> getEnterpriseValue )
  }
    
}
object Company{
  val order = List("ID", "–¼Ì", "R", "’l", 
                   "Å”„", "Å”„”", "Å”ƒ", "Å”ƒ”",
                   "Œ»’l", "‘OI", "‘O”ä", "o—ˆ",
                   "”ƒc", "”ƒcT·", "”„c", "”„cT·",
                   "”N‚", "”N‚“ú", "”NˆÀ", "”NˆÀ“ú",
                   "—˜", "‰v", "«", "ROE", "©",
                   "PER", "PBR",
                   "ŒˆZ", "—D‘Ò", "—“ú",
                   "”­s", "‰¿", "s", "•ª—Ş",
                   "‘ã•\", "İ—§", "ãê", "ŒˆŠú",
                   "]˜A", "]’P", "—î", "û",
                   "—¦", "Š”‰¿", "Šé‰¿", "XV")
  val abc = {
    for(char <- "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
      yield char.toString
    }.toList
  val columns = abc ::: abc.map("A"+_) ::: abc.map("B"+_)
  val columnsMap = order.zip(columns).toMap
  val reColumn = """y(.*?)z""".r
  
  val reJp = """[0-9]{4}""".r
  val reUs = """[A-Z]{1,5}""".r
  
  def apply(code:String, row:Int) :Company = {
    code match {
      case reJp() => CompanyJp(code, row)
      case reUs() => CompanyUs(code, row)
    }
  }
  
  def replaceColumn(data:Map[String, String], row:String) :List[String] = {
    order.map(x =>
      reColumn.replaceAllIn(data.getOrElse(x, "-"), m => columnsMap(m.group(1)) + row)
    )
  }  
}
