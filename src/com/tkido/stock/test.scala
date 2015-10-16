package com.tkido.stock

import com.tkido.stock.spider.SpiderJpConsolidate
import com.tkido.stock.spider.SpiderJpProfile
import com.tkido.tools.Log

import com.tkido.tools.Html
import com.tkido.tools.Search

object test extends App {
  Log open Config.logLevel
  
  Log d SpiderJpConsolidate("3085")
  Log d SpiderJpProfile("3085")

  Log close
}