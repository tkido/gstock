package com.tkido.stock.tdnet

import com.tkido.stock.Config
import com.tkido.tools.Log
import com.tkido.tools.Text
import com.tkido.tools.retry
import com.tkido.tools.MyString
import java.io.File
import scala.xml._

object XbrlDownloaderJp {
  val reTanshin = """決算短信""".r
  val reXbrl = """(tdnet|tse)-..edjpsm.*?(\.xbrl|-ixbrl\.htm)$""".r
  
  def apply(code:String) {
    val root = new File(Config.xbrlPath, "/tdnet/"+code)
    if(!root.exists) root.mkdir
    
    val url = s"http://resource.ufocatch.com/atom/tdnetx/query/${code}"
    retry { XML.load(url) } foreach download
    
    def download(xml:Elem) {
      val tanshins = (xml \ "entry").filter(node => (node \ "title").text =~ reTanshin)
      val xbrls = tanshins.map(node =>
        (node \\ "@href").map(_.text).find(_ =~ reXbrl)
      ).collect{case Some(s) => s}
      
      for(xbrl <- xbrls){
        val file = new File(root, xbrl.split("/").last)
        if(!file.exists){
          retry { Text.read(xbrl) } foreach {txt =>
            Text.write(file.getPath, txt)
            XbrlDownloader.add(xbrl)
          }
        }
      }
    }
  }
}