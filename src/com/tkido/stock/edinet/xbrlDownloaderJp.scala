package com.tkido.stock.edinet

object XbrlDownloaderJp {
  import com.tkido.stock.Config
  import com.tkido.tools.Text
  import com.tkido.tools.retry
  import java.io.File
  import scala.xml._
  import scala.util.control.Exception._

  def apply(code:String) {
    val root = new File(Config.xbrlPath, "/edinet/"+code)
    if(!root.exists) root.mkdir
    
    val url = s"http://resource.ufocatch.com/atom/edinetx/query/${code}"
    val xmlOp = allCatch opt retry {XML.load(url)}
    if(xmlOp.isEmpty) return
    val xml = xmlOp.get
    
    def isUfo(node:Node) :Boolean = {
      val reUfo = """有価証券報告書""".r
      val title = (node \ "title").text
      reUfo.findFirstIn(title).isDefined
    }
    val ufos = (xml \ "entry").filter(isUfo)
    
    def getXbrl(node:Node) :String = {
      val reXbrl = """(jpcrp|jpfr).*?\.xbrl$""".r
      val hrefs = (node \\ "@href").map(_.text)
      hrefs.find(reXbrl.findFirstIn(_).isDefined).get
    }
    val xbrls = ufos.map(getXbrl)
    
    for(xbrl <- xbrls){
      val fileName = xbrl.split("/").last
      val file = new File(root, fileName)
      if(!file.exists) Text.write(file.getPath, Text.read(xbrl))
    }
  }
}