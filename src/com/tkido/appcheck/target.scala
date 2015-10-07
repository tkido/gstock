package com.tkido.appcheck

import com.tkido.tools.Log
import com.tkido.tools.Text

case class Target(id:String, name:String, appname:String, url:String)

object TargetParser {
  
  def apply(path:String) :List[Target] = {
    def lineToTarget(line:String) :Target = {
      
      val List(app_id, app_name, nation_id, nation_name, type_id, type_name, ipad_id, ipad_name, genre_id, genre_name, url) =
        line.split("\t").toList
      val target_id = List(app_id, nation_id, type_id, ipad_id, genre_id).mkString("_")
      val target_name = "『%s』".format(app_name) + List(nation_name, type_name, ipad_name, genre_name).mkString
      Target(target_id, target_name, app_name, url)
    }
    val lines = Text.readLines(path).tail
    lines.map(lineToTarget)
  }
}