package com.tkido.stock.xbrl

object XbrlFinder {
  import java.io.File
  
  def find(code:String) :List[String] = {
    def listFiles(filter:File => Boolean)(file:File): List[File] =
      if (file.isDirectory)
        file.listFiles.toList.flatMap(listFiles(filter))
      else
        List(file).filter(filter)
    def isXbrl(file:File) :Boolean =
      file.getName.endsWith(".xbrl")
    val root = new File("data/xbrl/xbrl/%s".format(code))
    val files = listFiles(isXbrl)(root)
    files.map(_.toString)
  }
}