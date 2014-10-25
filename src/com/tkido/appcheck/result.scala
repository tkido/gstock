package com.tkido.appcheck

import com.tkido.tools.Logger
import com.tkido.tools.Text
import java.io.File
  
case class Result(rank:String, updated:String) {
  override def toString :String = {
    "%s\t%s".format(rank, updated)
  }
}

object ResultParser {
  def apply(target:Target) :List[Result] = {
    def lineToResult(line:String) :Result = {
      val arr = line.split("\t")
      Result(arr(0), arr(1))
    }
    val path = new File(Config.dataPath, target.id + ".txt").toString
    Logger.debug(path)
    val lines = Text.readLines(path)
    lines.map(lineToResult)
  }
}

object ResultWriter {
  def apply(target:Target, results:List[Result]) :Unit = {
    val path = new File(Config.dataPath, target.id + ".txt").toString
    Logger.debug(path)
    Text.write(path, results.mkString("\n"))
  }
}