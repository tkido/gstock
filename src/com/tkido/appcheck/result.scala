package com.tkido.appcheck

import com.tkido.stock.Config
import com.tkido.tools.Log
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
      val List(rank, updated) = line.split("\t").toList
      Result(rank, updated)
    }
    val file = new File(Config.dataPath, target.id + ".txt")
    if(file.exists()){
      val lines = Text.readLines(file.toString)
      lines.map(lineToResult)
    }else{
      List()
    }
  }
}

object ResultWriter {
  def apply(target:Target, results:List[Result]) :Unit = {
    val path = new File(Config.dataPath, target.id + ".txt").toString
    Log d path
    Text.write(path, results.mkString("\n"))
  }
}