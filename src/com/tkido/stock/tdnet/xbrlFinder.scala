package com.tkido.stock.tdnet

object XbrlFinder {
  import com.tkido.stock.Config
  import java.io.File
  
  def apply(code:String) :List[String] = {
    def listFiles(filter:File => Boolean)(file:File): List[File] =
      if (file.isDirectory)
        file.listFiles.toList.flatMap(listFiles(filter))
      else
        List(file).filter(filter)
    def isXbrl(file:File) :Boolean = {
      val name = file.getName
      name.endsWith(".xbrl") || name.endsWith("-ixbrl.htm")
    }
    val root = new File(Config.xbrlPath, "/tdnet/"+code)
    val files = listFiles(isXbrl)(root)
    files.map(_.toString)
  }
}
