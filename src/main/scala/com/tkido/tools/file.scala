package com.tkido.tools

object File {
  import java.io.File
  
  def listFiles(filter:File => Boolean)(file:File): List[File] =
    if (file.isDirectory)
      file.listFiles.toList.flatMap(listFiles(filter))
    else
      List(file).filter(filter)
}