package com.tkido.statistics

object RankCorrelationIndex {
  def apply[T](data:Seq[T])
        (implicit orderer: T => Ordered[T]) :Double = {
    def square(x:Int) = x * x
    val s = data.size
    val range = Range(0, s)
    val pairs = data.zip(range).sortBy(_._1).map(_._2).zip(range)
    val d = pairs.map(p => square(p._1 - p._2)).sum
    val rci = (1.0 - (6.0 * d / (s * (s * s -1)))) * -100
    rci
  }
}