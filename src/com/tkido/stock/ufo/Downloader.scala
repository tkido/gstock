package com.tkido.stock.ufo

object Downloader {
  import com.tkido.tools.TextFile
  import java.io.File
  import java.net.URL
  import scala.io.Source
  import scala.xml._
  
  def download(code:String) {
    val root = new File(Config.rootPath, code)
    if(!root.exists) root.mkdir
    
    val url = "http://resource.ufocatch.com/atom/edinetx/query/" + code
    val xml = XML.load(url)
    
    def isUfo(node:Node) :Boolean = {
      val reUfo = """�L���،��񍐏�""".r
      val title = (node \ "title").text
      reUfo.findFirstIn(title).isDefined
    }
    val ufos = (xml \ "entry").filter(isUfo)
    //println(ufos)
    
    def getXbrl(node:Node) :String = {
      val reXbrl = """\.xbrl$""".r
      val hrefs = (node \\ "@href").map(_.text)
      hrefs.find(reXbrl.findFirstIn(_).isDefined).get
    }
    val xbrls = ufos.map(getXbrl) 
    println(xbrls)
    
    for(xbrl <- xbrls){
      val data = Source.fromURL(xbrl).getLines.mkString("\n")
      val fileName = xbrl.split("/").last
      val filePath = new File(root, fileName).getPath
      println(fileName)
      println(filePath)
      
      TextFile.write(filePath, data)
    }
  }
}