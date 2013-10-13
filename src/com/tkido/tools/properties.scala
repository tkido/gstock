package com.tkido.tools

object Properties {
  import java.io.FileInputStream
  import java.util.Properties
  import scala.collection.JavaConverters._
  
  def apply[T](path:String, func:String => T) :Map[String, T] = {
    val prop = new Properties
    prop.load(new FileInputStream(path))
    prop.asScala.toMap.mapValues(func)
  }
  def apply(path:String) :Map[String, String] =
    apply(path, s => s)
  
}