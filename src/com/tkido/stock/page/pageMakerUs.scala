package com.tkido.stock.page

object PageMakerUs {
  import com.tkido.stock.spider.Company
  import com.tkido.tools.Text
  
  private val templete = Text.read("data/rss/templateUS.html")
  
  def apply(company:Company){
    val data = company.data
    
    val code     = data.getOrElse("ID", "")
    val name     = data.getOrElse("名称", "")
    val feature  = data.getOrElse("特色", "")
    
    val title = code + " " + name
    
    val html = templete.format(title, "", title, feature, "", "")
    Text.write("data/rss/%s.html".format(code), html)
  }
}