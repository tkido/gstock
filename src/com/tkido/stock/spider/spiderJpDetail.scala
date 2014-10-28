package com.tkido.stock.spider

object SpiderJpDetail {
  import com.tkido.tools.Html
  import com.tkido.tools.Logger
  import com.tkido.tools.tryOrElse
  
  def apply(code:String) :Map[String, String] = {
    Logger.debug("SpiderJpDetail Spidering ", code)
    
    def get :Map[String, String] = {
      val html = Html("http://stocks.finance.yahoo.co.jp/stocks/detail/?code=%s".format(code))
      
      val marketName = {
        val raw = html.getNextLineOf("""<div class="stockMainTab clearFix">""".r).replaceFirst("""PTS.*""", "")
        raw match {
          case "東証1部"  => "東1"
          case "東証2部"  => "東2"
          case "東証JQS"  => "東J"
          case "東証JQG"  => "東J"
          case "マザーズ" => "東M"
          case "名証1部"  => "名1"
          case "名証2部"  => "名2"
          case "札証"     => "札"
          case "福証"     => "福"
          case _          => raw
        }
      }

      val outstanding =
        html.getPreviousLineOf("""<dt class="title">発行済株式数""".r).dropRight(12)
      val currentPrice =
        html.getGroupOf("""^.*?<td class="stoksPrice">(.*?)</td>""".r).replaceFirst("---", " ")
      val lastClose =
        html.getPreviousLineOf("""<dt class="title">前日終値""".r).dropRight(7)
      val ratioLast =
        html.getGroupOf("""<td class="change"><span class="yjSt">前日比</span><span class=".*? yjMSt">.*?（(.*?)%）</span></td>""".r)
      val valume =
        html.getPreviousLineOf("""<dt class="title">出来高""".r).dropRight(8).replaceAll(",", "").replaceFirst("-", "0")
      val highest =
        html.getPreviousLineOf("""<dt class="title">年初来高値""".r).dropRight(10).replaceAll(",", "").replaceFirst("更新", "")
      val highestDate =
        html.getPreviousLineOf("""<dt class="title">年初来高値""".r).takeRight(10).init.tail
      val lowest =
        html.getPreviousLineOf("""<dt class="title">年初来安値""".r).dropRight(10).replaceAll(",", "").replaceFirst("更新", "")
      val lowestDate =
        html.getPreviousLineOf("""<dt class="title">年初来安値""".r).takeRight(10).init.tail
      val dividendYield =
        html.getPreviousLineOf("""<dt class="title">配当利回り""".r).replaceFirst("""（.*""", "").replaceFirst("---", "0")
      val per =
        html.getPreviousLineOf("""<dt class="title">PER""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "0")
      val pbr =
        html.getPreviousLineOf("""<dt class="title">PBR""".r).replaceFirst("""倍.*""", "").replaceFirst("""\(.\) """, "").replaceFirst("---", "-")
      val buyOnCredit =
        html.getPreviousLineOf("""<dt class="title">信用買残""".r).replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-")
      val sellOnCredit =
        html.getPreviousLineOf("""<dt class="title">信用売残""".r).replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-")
      val buyOnCreditDelta =
        html.getPreviousLineOf("""<dt class="title"><span class="icoL">前週比</span>.*?shinyoubaizann_zensyuuhi""".r).replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-")
      val sellOnCreditDelta =
        html.getPreviousLineOf("""<dt class="title"><span class="icoL">前週比</span>.*?shinyouuriage_zensyuuhi""".r).replaceFirst("""株.*""", "").replaceAll(",", "").replaceFirst("---", "-")
      
      def divCode(content:String, div:String) :String =
        if(content == "-") "-" else "=%s/%s".format(content, div)
      
      Map("発行"     -> outstanding,
          "現値"     -> currentPrice,
          "前終"     -> lastClose,
          "前比"     -> ratioLast,
          "出来"     -> divCode(valume, "【発行】"),
          "買残"     -> divCode(buyOnCredit, "【発行】"),
          "買残週差" -> divCode(buyOnCreditDelta, "【発行】"),
          "売残"     -> divCode(sellOnCredit, "【発行】"),
          "売残週差" -> divCode(sellOnCreditDelta, "【発行】"),
          "年高"     -> divCode(highest, "【値】"),
          "年高日"   -> highestDate,
          "年安"     -> divCode(lowest, "【値】"),
          "年安日"   -> lowestDate,
          "市"       -> marketName,
          "利"       -> dividendYield,
          "PER"      -> per,
          "PBR"      -> pbr )
    }
    tryOrElse(get _, Map())
  }
}