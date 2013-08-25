package com.tkido.stock.rss

object ChartMaker {
  import com.tkido.tools.TextFile
  private val templete = TextFile.read("data/rss/template.html")
  
  def make(company:Company){
    val code = company.data("ID")
    val name = company.data("����")
    val feature = company.data("���F")
    val business = company.data("����")
    
    def getDate() :String = {
      val rgexDate = """\([0-9]{4}\.[0-9]{1,2}\)""".r
      val m = rgexDate.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    def getHeader() :String = {
      val rgexHeader = """�y.*?�z""".r
      val m = rgexHeader.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    def getOther() :String = {
      val rgexHeader = """�y.*?�z.*?(�y.*)""".r
      val m = rgexHeader.findFirstMatchIn(business)
      if(m.isDefined) m.get.group(1).replaceFirst("""\([0-9]{4}\.[0-9]{1,2}\)""", "")
      else ""
    }
    def getRows() :String = {
      val rawStr = business.replaceFirst("""\([0-9]{4}\.[0-9]{1,2}\)""", "").replaceFirst("""�y.*?�z""", "").replaceFirst("""�y.*""", "")
      val rawRows = rawStr.split('�A')
      
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
    TextFile.write("data/rss/%s.html".format(code), html)
  }
}

