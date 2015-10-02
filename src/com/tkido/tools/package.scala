package com.tkido

package object tools {
  def tryOrElse[T](function:() => T, default:T) :T = {
    try{
      function()
    }catch{
      case error => {
        Log f error
        default
      }
    }
  }
}