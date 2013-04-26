package com.tkido.stock.rss

object ChartMaker {
  import scala.util.matching.Regex
  
  private val templete = """
<html>
  <head>
    <meta charset="utf-8" />
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
                       'height':600};

        // Instantiate and draw our chart, passing in some options.
        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
    </script>
  </head>

  <body>
    <h1>%s</h1>
    <p>%s</p>
    <p>%s</p>
    <!--Div that will hold the pie chart-->
    <div id="chart_div"></div>
  </body>
</html>
"""
  
  def make(code:String, name:String, feature:String, data:String){
    def getDate() :String = {
      val rgexDate = """\([0-9]{4}\.[0-9]{1,2}\)""".r
      val m = rgexDate.findFirstMatchIn(data)
      if(m.isDefined) m.get.group(0)
      else ""
    }
    val date = getDate
    val title = name + date
    
    val other = ""
    val html = templete.format(title, data, title, feature, other)
    println(date)
  }
}

object main extends App {
  ChartMaker.make("3085",
                  "アークランドサービス",
                  "カツ丼専門店「かつや」を直営、ＦＣで展開。天丼育成中。親会社は新潟地盤のホームセンター",
                  "【連結事業】かつや直営飲食59、ＦＣ33、他直営飲食6、他2(2012.12)")
}

/*
          ['かつや直営飲食', 59],
          ['ＦＣ', 33],
          ['他直営飲食', 6],
          ['他', 2]
*/