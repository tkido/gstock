package com.tkido.tools

import com.ibm.icu.text.Transliterator

object Date {
  val transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
  val reDateJp = """(平成)(\d{1,2})年(\d{1,2})月(\d{1,2})日""".r
  
  def fromJpToSimple(source:String) :String = {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
     * 平成26年2月7日 → 2014-02-07
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    transliterator.transliterate(source) match {
      case reDateJp(era, year, month, day) =>
        val yearInt = era match {
          case "平成" => 1988 + year.toInt
          case _ => 0
        }
        "%04d-%02d-%02d".format(yearInt, month.toInt, day.toInt)
      case _ => "-"
    }
  }
}