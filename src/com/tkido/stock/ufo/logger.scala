package com.tkido.stock.ufo

object Logger{
  import java.io.FileOutputStream
  import java.io.OutputStreamWriter
  import java.util.Date
  
  val DEBUG = 0
  val INFO  = 1
  val WARN  = 2
  val ERROR = 3
  val FATAL = 4
  
  private val encode = "UTF-8"
  private val append = true
  private val template = "%tY_%<tm%<td_%<tH%<tM_%<tS"
  
  var level = FATAL
  val now = new Date
  val today = "%tY/%<tm/%<td".format(now)
  
  val started = template.format(now)
  private val fileName = "log/%s.log".format(started)
  
  private val stream = new FileOutputStream(fileName, append)
  private val writer = new OutputStreamWriter(stream, encode)
  
  log5("***** STARTED at %s *****".format(started))
  println("STARTED at %s".format(started))
  
  private def write(args: Seq[Any]){
    writer.write(args.mkString("", "", "\n"))
  }
  def close() {
    val ended = template.format(new Date)
    log5("***** ENDED at %s *****".format(ended))
    writer.close
    println("ENDED at %s".format(ended))
  }

  def log1(args: Any*) {if (level <= 1) write(args)}
  def log2(args: Any*) {if (level <= 2) write(args)}
  def log3(args: Any*) {if (level <= 3) write(args)}
  def log4(args: Any*) {if (level <= 4) write(args)}
  def log5(args: Any*) {if (level <= 5) write(args)}
}