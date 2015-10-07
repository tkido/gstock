package com.tkido.stock.page

import com.tkido.tools.Text

object PageMakerUs {
  private val templete = Text.read("data/rss/templateUS.html")
  
  def apply(data:Map[String, String]){
    val code     = data.getOrElse("ID", "")
    val name     = data.getOrElse("名称", "")
    val feature  = data.getOrElse("特色", "")
    
    val title = code + " " + name
    
    val html = templete.format(title, "", title, feature, "", "")
    Text.write("data/rss/%s.html".format(code), html)
  }
}