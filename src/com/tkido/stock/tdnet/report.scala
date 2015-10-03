package com.tkido.stock.tdnet

import com.tkido.tools.Html

case class Report[T](year:Int, quarter:Int, date:String, month:String, data:List[T]) extends Ordered[Report[T]]{
  private def identify(year:Int, quarter:Int) :Int = year * 10 + quarter
  
  def id = identify(year, quarter)
  
  def lastQuarterId:Option[Int] =
    if(quarter == 1) None
    else Some(identify(year, quarter-1))
  
  def lastYearId:Int = identify(year-1, quarter)
  
  def compare(that:Report[T]) =
    if(year != that.year) year - that.year
    else quarter - that.quarter
    
  override def toString =
    Html.toTrTd(month,
                quarter,
                data(0),
                data(1),
                data(2),
                data(3),
                date)
}
