package com.tkido.stock.spider

import com.tkido.tools.Html
import com.tkido.tools.Log
import com.tkido.tools.Search

object SpiderJpDetail {
  def divCode(content:String, div:String) :String =
    if(content == "-") "-" else "=%s/%s".format(content, div)
  val rule = List(
    Search("発行", """<dt class="title">発行済株式数""".r, Search.LAST, _.dropRight(12)),
    Search("現値", """^.*?<td class="stoksPrice">(.*?)</td>""".r, Search.GROUP, _.replaceFirst("---", " ")),
    Search("前終", """<dt class="title">前日終値""".r, Search.LAST, _.dropRight(7)),
    Search("前比", """<td class="change"><span class="yjSt">前日比</span><span class=".*? yjMSt">.*?（(.*?)%）</span></td>""".r, Search.GROUP, s => s),
    Search("出来", """<dt class="title">出来高""".r, Search.LAST, raw => divCode(raw.dropRight(8).replaceAll(",", "").replaceFirst("-", "0"), "【発行】")),
    Search("買残", """<dt class="title">信用買残""".r, Search.LAST, raw => divCode(raw.replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-"), "【発行】")),
    Search("売残", """<dt class="title">信用売残""".r, Search.LAST, raw => divCode(raw.replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-"), "【発行】")),
    Search("買残週差", """<dt class="title"><span class="icoL">前週比</span>.*?shinyoubaizann_zensyuuhi""".r, Search.LAST, raw => divCode(raw.replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-"), "【発行】")),
    Search("売残週差", """<dt class="title"><span class="icoL">前週比</span>.*?shinyouuriage_zensyuuhi""".r, Search.LAST, raw => divCode(raw.replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-"), "【発行】")),
    Search("年高", """<dt class="title">年初来高値""".r, Search.LAST, raw => divCode(raw.dropRight(10).replaceAll(",", "").replaceFirst("更新", ""), "【値】")),
    Search("年安", """<dt class="title">年初来安値""".r, Search.LAST, raw => divCode(raw.dropRight(10).replaceAll(",", "").replaceFirst("更新", ""), "【値】")),
    Search("年高日", """<dt class="title">年初来高値""".r, Search.LAST, _.takeRight(10).init.tail),
    Search("年安日", """<dt class="title">年初来安値""".r, Search.LAST, _.takeRight(10).init.tail),
    Search("利", """<dt class="title">配当利回り""".r, Search.LAST, _.replaceFirst("""（.*""", "").replaceFirst("---", "0")),
    Search("PER", """<dt class="title">PER""".r, Search.LAST, _.replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "0")),
    Search("PBR", """<dt class="title">PBR""".r, Search.LAST, _.replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "-")),
    Search("市", """<div id="deal">""".r, Search.NEXT, _.replaceFirst("""PTS.*""", "") match{
      case "東証1部"  => "1"
      case "東証2部"  => "2"
      case "東証JQS"  => "J"
      case "東証JQG"  => "JG"
      case "マザーズ" => "M"
      case "名証1部"  => "名1"
      case "名証2部"  => "名2"
      case "札証"     => "札"
      case "福証"     => "福"
      case s:String   => s
    }))
  
  def apply(code:String) :Map[String, String] = {
    Log d s"SpiderJpDetail Spidering ${code}"
    val html = Html(s"https://stocks.finance.yahoo.co.jp/stocks/detail/?code=${code}")
    html.search(rule)
  }
}