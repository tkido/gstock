package com.tkido.tools

class MyString(s:String) {
  import scala.util.matching.Regex
  
  def matched(re:Regex) :Boolean =
    re.findFirstIn(s).isDefined
  
  def =~(re:Regex) :Boolean = matched(re)
  def !~(re:Regex) :Boolean = !matched(re)
}