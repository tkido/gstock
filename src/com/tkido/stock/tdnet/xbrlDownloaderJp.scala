package com.tkido.stock.tdnet

import com.tkido.stock.Config
import com.tkido.tools.Log
import com.tkido.tools.Text
import com.tkido.tools.retry
import java.io.File
import scala.xml._

object XbrlDownloaderJp {
  def apply(code:String) {
    val root = new File(Config.xbrlPath, "/tdnet/"+code)
    if(!root.exists) root.mkdir
    
    val url = "http://resource.ufocatch.com/atom/tdnetx/query/%s".format(code)
    retry { XML.load(url) } foreach download
    
    def download(xml:Elem) {
      def isTanshin(node:Node) :Boolean = {
        val reTanshin = """決算短信""".r
        val title = (node \ "title").text
        reTanshin.findFirstIn(title).isDefined
      }
      val tanshins = (xml \ "entry").filter(isTanshin)
      
      def getXbrl(node:Node) :Option[String] = {
        val reXbrl = """(tdnet|tse)-..edjpsm.*?(\.xbrl|-ixbrl\.htm)$""".r
        val hrefs = (node \\ "@href").map(_.text)
        hrefs.find(reXbrl.findFirstIn(_).isDefined)
      }
      val xbrls = tanshins.map(getXbrl).collect{case Some(s) => s}
      
      for(xbrl <- xbrls){
        val fileName = xbrl.split("/").last
        val file = new File(root, fileName)
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