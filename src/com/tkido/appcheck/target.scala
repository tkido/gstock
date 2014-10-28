package com.tkido.appcheck

case class Target(id:String, name:String, appname:String, url:String)

object TargetParser {
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  
  def apply(path:String) :List[Target] = {
    def lineToTarget(line:String) :Target = {
      val arr = line.split("\t")
      val (apptitle, nation_id, nation_name, type_id, type_name, ipad_id, ipad_name, genre_id, genre_name, url) =
          (arr(0), arr(1), arr(2), arr(3), arr(4), arr(5), arr(6), arr(7), arr(8), arr(9))
      val target_id = List(nation_id, type_id, ipad_id, genre_id).mkString("_")
      val target_name = List(nation_name, type_name, ipad_name, genre_name).mkString
      Target(target_id, target_name, apptitle, url)
    }
    val lines = Text.readLines(path).tail
    lines.map(lineToTarget)
  }
}