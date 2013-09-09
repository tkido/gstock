package com.tkido.stock.rss

object ChartMaker {
  import com.tkido.tools.Text
  
  private val templete = Text.read("data/rss/template.html")
  
  def make(company:Company){
    val data = company.data
    
    val business = data.getOrElse("事業", "")
    val name     = data.getOrElse("名称", "")
    val feature  = data.getOrElse("特色", "")
    val table    = data.getOrElse("表", "")
    
    val reDate = """\(\d{4}\.\d{1,2}\)"""
    val reHeader = "【.*?】"
    val reOther = "【.*"
    
    def getDate() :String = {
      val opt = reDate.r.findFirstMatchIn(business)
      if(opt.isEmpty) "" else opt.get.group(0)
    }
    def getHeader() :String = {
      val opt = reHeader.r.findFirstMatchIn(business)
      if(opt.isEmpty) "" else opt.get.group(0)
    }
    def getOther() :String = {
      val opt = reOther.r.findFirstMatchIn(business.replaceFirst(reHeader, ""))
      if(opt.isEmpty) "" else opt.get.group(0).replaceFirst(reDate, "")
    }
    
    def getRows() :String = {
      val str = business.replaceFirst(reDate, "").replaceFirst(reHeader, "").replaceFirst(reOther, "")
      val rows = str.split('、')
      
      def stringToPairs(raw: String): Pair[String, String] = {
        val reData = """(.*?)(\d+)(\(\d+\))?""".r
        val opt = reData.findFirstMatchIn(raw)
        if(opt.isEmpty) "" -> ""
        else{
          val m = opt.get
          val g3 = if(m.group(3) == null) "" else m.group(3)
          Pair(m.group(1)+g3, m.group(2))
        }
      }
      val pairs = rows.map(stringToPairs)
      pairs.map(x => """['%s', %s]""".format(x._1, x._2) ).mkString(",\n")
    }
    
    val code = data.getOrElse("ID", "")
    val title = code + " " + name + getHeader + getDate
    
    val html = templete.format(title, getRows, title, feature, getOther, table)
    Text.write("data/rss/%s.html".format(code), html)
  }
}

