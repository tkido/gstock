package com.tkido.stock.ufo

object XbrlDownloaderJp {
  import com.tkido.tools.Text
  import java.io.File
  import java.net.URL
  import scala.io.Source
  import scala.xml._
  
  def download(code:String) {
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
    
    if(xbrls.nonEmpty){
      val root = new File(Config.rootPath, code)
	  if(!root.exists) root.mkdir
	  
      for(xbrl <- xbrls){
        val fileName = xbrl.split("/").last
        val file = new File(root, fileName)
        if(!file.exists){
          val data = Source.fromURL(xbrl, "UTF-8").getLines.mkString("\n")
          Text.write(file.getPath, data)
        }
      }
    }
  }
}