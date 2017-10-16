package com.tkido.stock.spider

import com.ibm.icu.text.Normalizer
import com.ibm.icu.text.Transliterator
import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.Search
import com.tkido.tools.retry

object SpiderJpProfile {
  val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
  
  val rule = List(
      Search("名称", """<meta http-equiv="Refresh" content="60">""".r, Search.NEXT, tidyName(_)),
      Search("特色", """<th width="1%" nowrap>特色</th>""".r, Search.NEXT, _.replaceFirst(""" \[企業特色\]""", "")),
      Search("事業", """<th nowrap>連結事業</th>""".r, Search.NEXT, s => s),
      Search("分類", """<th nowrap>業種分類</th>""".r, Search.NEXT, _.replaceAll("・", "")),
      Search("設立", """<th nowrap>設立年月日</th>""".r, Search.NEXT, _.slice(0, 4)),
      Search("上場", """<th nowrap>上場年月日</th>""".r, Search.NEXT, _.slice(0, 4)),
      Search("決期", """<th nowrap>決算</th>""".r, Search.NEXT, _.replaceAll("末日", "").replaceAll(""" \[決算情報　年次\]""", "")),
      Search("従連", """<th width="1%">従業員数<br><span class="yjSt">（連結）</span></th>""".r, Search.NEXT, _.dropRight(1)),
      Search("従単", """<th width="1%">従業員数<br><span class="yjSt">（単独）</span></th>""".r, Search.NEXT, _.dropRight(1)),
      Search("齢", """<th nowrap>平均年齢</th>""".r, Search.NEXT, _.dropRight(1)),
      Search("収", """<th nowrap>平均年収</th>""".r, Search.NEXT, _.dropRight(3).replaceAll(",", "")),
      Search("代表", """<th nowrap>代表者名</th>""".r, Search.NEXT, _.replaceAll("　", "").replaceAll(""" \[役員\]""", ""))
    )
  
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderJpProfile Spidering ${code}"
    retry { Html(s"https://stocks.finance.yahoo.co.jp/stocks/profile/?code=${code}") } match {
      case Some(html) => html.search(rule)
      case None       => Map()
    }
  }
  
  def tidyName(s:String) :String = {
    val tmp1 = s.dropRight(27).replaceAll("""\(株\)|・|　|＆|ホールディングス?|コーポレーション|カンパニー|グループ|本社|ジャパン$""", "")
    val tmp2 = Normalizer.normalize(tmp1, Normalizer.NFKC)
    if(tmp2.size > 10){
      transliterator.transliterate(tmp2)
    }else{
      tmp2
    }
  }
  
}