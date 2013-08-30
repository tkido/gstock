package com.tkido.stock.rss

object ChartMaker {
  import com.tkido.tools.Text
  private val templete = Text.read("data/rss/template.html")
  
  def make(company:Company){
    val code = company.data("ID")
    val name = company.data("名称")
    val feature = company.data("特色")
    val business = company.data("事業")
    
    def getDate() :String = {
      val rgexDate = """\([0-9]{4}\.[0-9]{1,2}\)""".r
      val m = rgexDate.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    def getHeader() :String = {
      val rgexHeader = """【.*?】""".r
      val m = rgexHeader.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    def getOther() :String = {
      val rgexHeader = """【.*?】.*?(【.*)""".r
      val m = rgexHeader.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(1).replaceFirst("""\([0-9]{4}\.[0-9]{1,2}\)""", "")
      else ""
    }
    def getRows() :String = {
      val rawStr = business.replaceFirst("""\([0-9]{4}\.[0-9]{1,2}\)""", "").replaceFirst("""【.*?】""", "").replaceFirst("""【.*""", "")
      val rawRows = rawStr.split('、')
      
      def stringToPairs(raw: String): Pair[String, String] = {
        val rgex = """(.*?)([0-9]+)(\([0-9]+\))?""".r
        val m = rgex.findFirstMatchIn(raw)
        if(m.isDefined){
          val g3 = m.get.group(3)
          val profitability = if(g3 == null) "" else g3
          Pair(m.get.group(1)+profitability, m.get.group(2))
        }else{
          Pair("", "")
        }
      }
      val pairs = rawRows.map(stringToPairs)
      def pairToString(pair: Pair[String, String]): String = {
        val (name, number) = pair
        """['%s', %s]""".format(name, number)
      }
      val strings = pairs.map(pairToString)
      val string = strings.mkString(",\n")
      string
    }
    val date = getDate
    val other = getOther
    val header = getHeader
    val rows = getRows
    val title = name + header + date
    
    val html = templete.format(title, rows, title, feature, other)
    Text.write("data/rss/%s.html".format(code), html)
  }
}

