package com.tkido.stock.tdnet

object XbrlDownloaderJp {
  import com.tkido.stock.Config
  import com.tkido.tools.Text
  import java.io.File
  import scala.xml._
  
  def apply(code:String) {
    val root = new File(Config.xbrlPath, "/tdnet/"+code)
    if(!root.exists) root.mkdir
    
    val url = "http://resource.ufocatch.com/atom/tdnetx/query/%s".format(code)
    val xml = XML.load(url)
    
    def isTanshin(node:Node) :Boolean = {
      val reTanshin = """決算短信""".r
      val title = (node \ "title").text
      reTanshin.findFirstIn(title).isDefined
    }
    val tanshins = (xml \ "entry").filter(isTanshin)
    
    def getXbrl(node:Node) :Option[String] = {
      val reXbrl = """tdnet-..edjpsm.*?\.xbrl$""".r
      val hrefs = (node \\ "@href").map(_.text)
      hrefs.find(reXbrl.findFirstIn(_).isDefined)
    }
    val xbrls = tanshins.map(getXbrl).collect{case Some(s) => s}
    
    for(xbrl <- xbrls){
      val fileName = xbrl.split("/").last
      val file = new File(root, fileName)
      if(!file.exists) Text.write(file.getPath, Text.read(xbrl))
    }
  }
}