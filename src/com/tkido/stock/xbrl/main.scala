package com.tkido.stock.xbrl

object main extends App {
  println("xbrl start")
  XbrlParser.parse("data/xbrl/3085/XBRL_20130617_105616/S000D3P0/jpfr-asr-E03513-000-2012-12-31-01-2013-03-25.xbrl")
}