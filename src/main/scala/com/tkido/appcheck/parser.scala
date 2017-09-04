package com.tkido.appcheck

import com.tkido.tools.Text

object Parser {
  def apply(path:String) :List[Target] = {
    def lineToTarget(line:String) :Target = {
      val arr = line.split("\t")
      val (nation_id, type_id, nation_name, type_name, apptitle, url) = (arr(0), arr(1), arr(2), arr(3), arr(4), arr(5))
      Target(nation_id + "_" + type_id, nation_name + type_name, apptitle, url)
    }
    val lines = Text.readLines(path).tail
    lines.map(lineToTarget)
  }
}