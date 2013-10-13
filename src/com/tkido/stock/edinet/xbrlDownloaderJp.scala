package com.tkido.stock.edinet

object XbrlDownloaderJp {
  import com.tkido.stock.Config
  import com.tkido.tools.Text
  import java.io.File
  import scala.xml._
  
  def apply(code:String) {
    val root = new File(Config.xbrlPath, "/edinet/"+code)
    if(!root.exists) root.mkdir
    
    val url = "http://resource.ufocatch.com/atom/edinetx/query/%s".format(code)
    val xml = XML.load(url)
    
    def isUfo(node:Node) :Boolean = {
      val reUfo = """有価証券報告書""".r
      val title = (node \ "title").text
      reUfo.findFirstIn(title).isDefined
    }
    val ufos = (xml \ "entry").filter(isUfo)
    
    def getXbrl(node:Node) :String = {
      val reXbrl = """\.xbrl$""".r
      val hrefs = (node \\ "@href").map(_.text)
      hrefs.find(reXbrl.findFirstIn(_).isDefined).get
    }
    val xbrls = ufos.map(getXbrl)
    
    for(xbrl <- xbrls){
      val fileName = xbrl.split("/").last
      val file = new File(root, fileName)
      if(!file.exists) Text.write(file.getPath, Text.fromURL(xbrl))
    }
  }
}