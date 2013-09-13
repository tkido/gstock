package com.tkido.tools

object Logger{
  import java.io.FileOutputStream
  import java.io.OutputStreamWriter
  import java.util.Date
  
  val DEBUG = 1
  val INFO  = 2
  val WARN  = 3
  val ERROR = 4
  val FATAL = 5
  
  var level = FATAL
  
  private val template = "%tY_%<tm%<td_%<tH%<tM_%<tS"
  
  val started = template.format(new Date)
  private val fileName = "log/%s.log".format(started)
  
  private val encode = "utf-8"
  private val append = true
  private val stream = new FileOutputStream(fileName, append)
  private val writer = new OutputStreamWriter(stream, encode)
  
  fatal("STARTED at %s".format(started))
  println("STARTED at %s".format(started))
  
  def close() {
    val ended = template.format(new Date)
    fatal("ENDED at %s".format(ended))
    println("ENDED at %s".format(ended))
    writer.close()
  }
  
  private def write(arg: Any) {
    writer.write(arg.toString()+"\n")
  }
  def debug(arg: => Any) { if (level <= DEBUG) write(arg) }
  def info (arg: => Any) { if (level <= INFO ) write(arg) }
  def warn (arg: => Any) { if (level <= WARN ) write(arg) }
  def error(arg: => Any) { if (level <= ERROR) write(arg) }
  def fatal(arg: => Any) { if (level <= FATAL) write(arg) }
  
  def isDebug = level <= DEBUG
  def isInfo  = level <= INFO
  def isWarn  = level <= WARN
  def isError = level <= ERROR
  def isFatal = level <= FATAL

}