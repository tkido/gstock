package com.tkido.stock.tdnet

import com.tkido.tools.Html

case class Report[T](year:Int, quarter:Int, date:String, month:String, data:List[T]) extends Ordered[Report[T]]{
  private def hash(year:Int, quarter:Int) :Int = year * 10 + quarter
  
  def id = hash(year, quarter)
  def compare(that:Report[T]) = id - that.id
  
  def lastQuarterId:Option[Int] = quarter match{
    case 1 => None
    case _ => Some(hash(year, quarter-1))
  }
  def lastYearId:Int = hash(year-1, quarter)
    
  override def toString =
    Html.toTrTd(month,
                quarter,
                data(0),
                data(1),
                data(2),
                data(3),
                date)
}
