package com.tkido.stock.spider

object SpiderJpProfile {
  import com.tkido.tools.Html
  import com.tkido.tools.Log
  import com.tkido.tools.tryOrElse
  
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderJpProfile Spidering ${code}"
    
    def get :Map[String, String] = {
      val html = Html("http://stocks.finance.yahoo.co.jp/stocks/profile/?code=%s".format(code))
      
      val name = {
        val raw = html.getNextLineOf("""<meta http-equiv="Refresh" content="60">""".r)
        raw.dropRight(27).replaceFirst("""\(株\)""", "")
      }
      val feature =
        html.getNextLineOf("""<th width="1%" nowrap>特色</th>""".r).replaceFirst(""" \[企業特色\]""", "")
      val consolidated =
        html.getNextLineOf("""<th nowrap>連結事業</th>""".r)
      val category = {
        html.getNextLineOf("""<th nowrap>業種分類</th>""".r).replaceAll("業", "").replaceAll("・", "")
      }
      val representative = {
        val raw = html.getNextLineOf("""<th nowrap>代表者名</th>""".r)
        raw.replaceAll("　", "").replaceAll(""" \[役員\]""", "")
      }
      val foundated =
        html.getNextLineOf("""<th nowrap>設立年月日</th>""".r).slice(0, 4)
      val listed =
        html.getNextLineOf("""<th nowrap>上場年月日</th>""".r).slice(0, 4)
      val settlement = {
        html.getNextLineOf("""<th nowrap>決算</th>""".r).replaceAll("末日", "").replaceAll(""" \[決算情報　年次\]""", "")
      }
      val singleEmployees =
        html.getNextLineOf("""<th width="1%">従業員数<br><span class="yjSt">（単独）</span></th>""".r).dropRight(1)
      val consolidatedEmployees =
        html.getNextLineOf("""<th width="1%">従業員数<br><span class="yjSt">（連結）</span></th>""".r).dropRight(1)
      val age =
        html.getNextLineOf("""<th nowrap>平均年齢</th>""".r).dropRight(1)
      val income =
        html.getNextLineOf("""<th nowrap>平均年収</th>""".r).dropRight(3).replaceAll(",", "")
      
      Map("名称" -> name,
          "特色" -> feature,
          "事業" -> consolidated,
          "分類" -> category,
          "設立" -> foundated,
          "上場" -> listed,
          "決期" -> settlement,
          "従連" -> consolidatedEmployees,
          "従単" -> singleEmployees,
          "齢"   -> age,
          "収"   -> income,
          "代表" -> representative)
    }
    tryOrElse(get _, Map())
  }
}