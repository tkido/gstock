package com.tkido.stock

import java.io.File
import java.security.MessageDigest

import com.tkido.stock.edinet.XbrlFinder
import com.tkido.tools.Log

object test extends App {
  Log.logging(Config.logLevel, main)
  
  def main() {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    
    val files = XbrlFinder("2685")
    val names = files.map(new File(_).getName)
    md.update(names.mkString.getBytes)
    Log f md.digest().map("%02x".format(_)).mkString
  }
}