package com.tkido.stock.edinet

object XbrlFinder {
  import com.tkido.stock.Config
  import java.io.File
  
  def apply(code:String) :List[String] = {
    def listFiles(filter:File => Boolean)(file:File): List[File] =
      if (file.isDirectory)
        file.listFiles.toList.flatMap(listFiles(filter))
      else
        List(file).filter(filter)
    def isXbrl(file:File) :Boolean =
      file.getName.endsWith(".xbrl")
    val root = new File(Config.xbrlPath, "/edinet/"+code)
    val files = listFiles(isXbrl)(root)
    
    def toYear(file:File) :Int =
      file.getName.slice(20, 24).toInt
    def distinct(list:List[File]) :List[File] =
      list.groupBy(toYear).mapValues(_.last).toList.map(_._2)
    distinct(files).sortBy(toYear).map(_.toString)
  }
}
