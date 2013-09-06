package com.tkido.stock.rss

object ChartMaker {
  import com.tkido.tools.Text
  
  private val templete = Text.read("data/rss/template.html")
  
  def make(company:Company){
    val data = company.data
    
    val code = data("ID")
    val business = data("事業")
    
    val reStringDate = """\([0-9]{4}\.[0-9]{1,2}\)"""
    
    def getDate() :String = {
      val reDate = reStringDate.r
      val m = reDate.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    
    def getHeader() :String = {
      val reHeader = """【.*?】""".r
      val m = reHeader.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    
    def getOther() :String = {
      val reHeader = """【.*?】.*?(【.*)""".r
      val m = reHeader.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(1).replaceFirst(reStringDate, "")
      else ""
    }
    
    def getRows() :String = {
      val rawStr = business.replaceFirst(reStringDate, "").replaceFirst("""【.*?】""", "").replaceFirst("""【.*""", "")
      val rawRows = rawStr.split('、')
      
      def stringToPairs(raw: String): Pair[String, String] = {
        val re = """(.*?)([0-9]+)(\([0-9]+\))?""".r
        val m = re.findFirstMatchIn(raw)
        if(m.isDefined){
          val g3 = m.get.group(3)
          val profitability = if(g3 == null) "" else g3
          Pair(m.get.group(1)+profitability, m.get.group(2))
        }else{
          Pair("", "")
        }
      }
      val pairs = rawRows.map(stringToPairs)
      pairs.map(x => """['%s', %s]""".format(x._1, x._2) ).mkString(",\n")
    }
    
    val date = getDate
    val other = getOther
    val header = getHeader
    val rows = getRows
    val title = data("名称") + header + date
    
    val html = templete.format(title, rows, title, data("特色"), other, data("表"))
    Text.write("data/rss/%s.html".format(code), html)
  }
}

