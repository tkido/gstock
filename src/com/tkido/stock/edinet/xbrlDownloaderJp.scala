package com.tkido.stock.edinet

import com.tkido.stock.Config
import com.tkido.tools.Text
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
      def isUfo(node:Node) :Boolean = {
        val title = (node \ "title").text
        reUfo.findFirstIn(title).isDefined
      }
      val ufos = (xml \ "entry").filter(isUfo)
      
      def getXbrl(node:Node) :String = {
        val hrefs = (node \\ "@href").map(_.text)
        hrefs.find(reXbrl.findFirstIn(_).isDefined).get
      }
      val xbrls = ufos.map(getXbrl)
      
      for(xbrl <- xbrls){
        val fileName = xbrl.split("/").last
        val file = new File(root, fileName)
        if(!file.exists){
          retry { Text.read(xbrl) } foreach { Text.write(file.getPath, _) }
        }
      }
    }
    
  }
}