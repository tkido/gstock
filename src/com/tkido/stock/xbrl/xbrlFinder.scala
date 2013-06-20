package com.tkido.stock.xbrl

object XbrlFinder {
  import java.io.File
  
  def find(code :String) :List[String] = {
    def listAllFiles(extension: String)(f: File): List[File] = {
      if (f.isDirectory)
        f.listFiles.toList.flatMap(listAllFiles(extension))
      else
        List(f).filter(_.getName.endsWith(extension))
    }
    val root = new File("data/xbrl/%s".format(code))
    val files = listAllFiles(".xbrl")(root)
    files.map(_.toString)
  }
}