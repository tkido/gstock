package com.tkido.stock.rss

object ChartMaker {
  import scala.util.matching.Regex
  import java.io.PrintWriter

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

  def writeFile(name: String, data: String) {
    val out = new PrintWriter("data/rss/%s.html".format(name))
    out.println(data)
    out.close
  }
    
  def make(code:String, name:String, feature:String, data:String){
    def getDate() :String = {
      val rgexDate = """\([0-9]{4}\.[0-9]{1,2}\)""".r
      val m = rgexDate.findFirstMatchIn(data)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    def getHeader() :String = {
      val rgexHeader = """【.*?】""".r
      val m = rgexHeader.findFirstMatchIn(data)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    def getOther() :String = {
      val rgexHeader = """【.*?】.*?(【.*)""".r
      val m = rgexHeader.findFirstMatchIn(data)
      if(m.isDefined) m.get.group(1).replaceFirst("""\([0-9]{4}\.[0-9]{1,2}\)""", "")
      else ""
    }
    def getRows() :String = {
      val rawStr = data.replaceFirst("""\([0-9]{4}\.[0-9]{1,2}\)""", "").replaceFirst("""【.*?】""", "").replaceFirst("""【.*""", "")
      val rawRows = rawStr.split('、')
      
      def stringToPairs(raw: String): Pair[String, String] = {
        val rgex = """(.*?)([0-9]+)""".r
        val m = rgex.findFirstMatchIn(raw)
        if(m.isDefined) Pair(m.get.group(1), m.get.group(2))
        else Pair("", "")
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
    writeFile(code, html)
  }
}

object main extends App {
  ChartMaker.make("3085",
                  "アークランドサービス",
                  "カツ丼専門店「かつや」を直営、ＦＣで展開。天丼育成中。親会社は新潟地盤のホームセンター",
                  "【連結事業】かつや直営飲食59、ＦＣ33、他直営飲食6、他2(2012.12)")
}
