package com.tkido.tools

object Date {
  import com.ibm.icu.text.Transliterator
  
  val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
  val reDateJp = """(平成)(\d{1,2})年(\d{1,2})月(\d{1,2})日""".r
  
  def fromJpToSimple(source:String) :String = {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
     * 平成26年2月7日 → 2014-02-07
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    def toDateString(era:String, year:String, month:String, day:String) :String = {
      val yearInt = era match {
        case "平成" => 1988 + year.toInt
        case _ => 0
      }
      "%04d-%02d-%02d".format(yearInt, month.toInt, day.toInt)
    }
    val halfSource = transliterator.transliterate(source)
    reDateJp.findFirstMatchIn(halfSource) match {
      case Some(m) => toDateString(m.group(1), m.group(2), m.group(3), m.group(4))
      case None    => "不明"
    }
  }

}