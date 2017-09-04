package com.tkido.tools

import java.io.FileInputStream
import scala.collection.JavaConverters._

object Properties {
  import java.util.Properties
  
  def apply(path:String) :Map[String, String] = {
    val prop = new Properties
    prop.load(new FileInputStream(path))
    prop.asScala.toMap
  }
  def apply[T](path:String, func:String => T) :Map[String, T] =
    apply(path).mapValues(func)
  
}