package com.tkido.stock.patrol

import com.tkido.tools.Properties

object Config {
  private val prop = Properties("data/patrol/conf.properties")
  val buildNumber = prop("buildNumber")
}