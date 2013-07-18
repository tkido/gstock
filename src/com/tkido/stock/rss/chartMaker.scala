package com.tkido.stock.rss

object ChartMaker {

  private val templete = """
<html>
  <head>
    <meta charset="shift-jis" />
    <title>%s</title>
    
    <!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">

      // Load the Visualization API and the piechart package.
      google.load('visualization', '1.0', {'packages':['corechart']});

      // Set a callback to run when the Google Visualization API is loaded.
      google.setOnLoadCallback(drawChart);

      // Callback that creates and populates a data table,
      // instantiates the pie chart, passes in the data and
      // draws it.
      function drawChart() {

        // Create the data table.
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Tpic');
        data.addColumn('number', 'Slices');
        data.addRows([%s]);

        // Set chart options
        var options = {'width':800,
                       'height':400};

        // Instantiate and draw our chart, passing in some options.
        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
    </script>
  </head>

  <body>
    <h1>%s</h1>
    <p>%s%s</p>
    <!--Div that will hold the pie chart-->
    <div id="chart_div"></div>
  </body>
</html>
"""
    
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
    TextFile.writeString("data/rss/%s.html".format(code), html)
  }
}

