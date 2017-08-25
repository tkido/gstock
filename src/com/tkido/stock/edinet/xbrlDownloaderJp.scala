package com.tkido.stock.edinet

import com.tkido.stock.Config
import com.tkido.tools.Text
import com.tkido.tools.strWrapper
import com.tkido.tools.retry
import java.io.File
import scala.xml._

object XbrlDownloaderJp {
  val reUfo = """有価証券報告書""".r
  val reXbrl = """(jpcrp|jpfr).*?\.xbrl$""".r
  
  def apply(code:String) {
    val root = new File(Config.xbrlPath, "/edinet/"+code)
    if(!root.exists) root.mkdir
    
    val url = s"http://resource.ufocatch.com/atom/edinetx/query/${code}"
    retry { XML.load(url) } foreach download
    
    def download(xml:Elem) {
      val ufos = (xml \ "entry").filter(node => (node \ "title").text =~ reUfo)
      val xbrls = ufos.map(node =>
        (node \\ "@href").map(_.text).find(_ =~ reXbrl).get
      )
      
      for(xbrl <- xbrls){
        val file = new File(root, xbrl.split("/").last)
        if(!file.exists){
          retry { Text.read(xbrl) } foreach { Text.write(file.getPath, _) }
        }
      }
    }
  }
}