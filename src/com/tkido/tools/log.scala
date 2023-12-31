package com.tkido.tools

object Log{
  import java.io.File
  import java.util.Date

  val DEBUG = 1
  val INFO  = 2
  val WARN  = 3
  val ERROR = 4
  val FATAL = 5
  
  private var level = 0
  
  val template = "%tY_%<tm%<td_%<tH%<tM_%<tS"
  val started = template format new Date
  
  val logger = 
    if(new File("log/").exists) new FileLogger
    else                        new PrintLogger
  
  f("STARTED at %s" format started)
  
  def log(arg: Any) {
    logger.log(arg)
  }
  
  def logging[T](logLevel:Int, f: => T) {
    try{
      open(logLevel)
      f
    }finally{
      close()
    }
  }
  
  private def open(logLevel:Int) {
    level = logLevel
  }
  
  private def close() {
    val ended = template format new Date
    f("ENDED at %s" format ended)
    logger.close()
  }
  
  def d(arg: => Any) { if (level <= DEBUG) log(arg) }
  def i(arg: => Any) { if (level <= INFO ) log(arg) }
  def w(arg: => Any) { if (level <= WARN ) log(arg) }
  def e(arg: => Any) { if (level <= ERROR) log(arg) }
  def f(arg: => Any) { if (level <= FATAL) log(arg) }
  
  def isDebug = (level <= DEBUG)
  def isInfo  = (level <= INFO)
  def isWarn  = (level <= WARN)
  def isError = (level <= ERROR)
  def isFatal = (level <= FATAL)
  
  abstract class Logger{
    def log(arg: Any)
    def close()
  }
  
  class PrintLogger extends Logger{
    def log(arg: Any) {
      println(arg)
    }
    
    def close() {}
  }
  
  class FileLogger extends Logger{
    import java.io.FileOutputStream
    import java.io.OutputStreamWriter
    
    private val fileName = "log/%s.log" format Log.started
    
    private val encode = "utf-8"
    private val append = true
    private val stream = new FileOutputStream(fileName, append)
    private val writer = new OutputStreamWriter(stream, encode)
    
    def log(arg: Any) {
      writer.write(arg.toString + '\n')
      println(arg)
    }
    
    def close() {
      writer.close()
    }
  }
}
