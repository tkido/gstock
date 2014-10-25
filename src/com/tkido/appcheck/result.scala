package com.tkido.appcheck

case class Result(rank:String, updated:String) {
  override def toString :String = {
    "%s\t%s".format(rank, updated)
  }
}

object ResultParser {
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  import java.io.File
  
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
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  import java.io.File
  
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