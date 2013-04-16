package com.tkido.stock.rss

import scala.io.{Codec, Source}
import java.io._
import java.net.URL
import scala.collection.{Iterable, IterableLike}

trait ScrapedHtml extends Iterable[String] with IterableLike[String, ScrapedHtml]{

  val src:Iterable[String]

  import scala.collection.generic.CanBuildFrom
  import scala.collection.mutable.{ListBuffer, Builder}

  def newTo(from:List[String]):ScrapedHtml
  def iterator = src.iterator
  override def newBuilder:Builder[String, ScrapedHtml] = new ListBuffer[String] mapResult {x => newTo(x) }
  implicit def canBuildFrom: CanBuildFrom[ScrapedHtml, String, ScrapedHtml] = new CanBuildFrom[ScrapedHtml, String, ScrapedHtml] {
    def apply(from: ScrapedHtml):Builder[String, ScrapedHtml] = newBuilder
    def apply() = newBuilder
  }

  def write( fileName:String ):Unit = {
    import scala.util.control.Exception._
    allCatch.opt{ 
      new BufferedWriter(new FileWriter(fileName))
    }.foreach{ bw =>
      allCatch.andFinally{ bw.close } {
        bw.write( src.mkString(System.getProperty("line.separator")))
      }
    }
  }

  def parse:ScrapedHtml
}

case class RawHtml(src:Iterable[String]) extends ScrapedHtml {
  def newTo(from:List[String]) = RawHtml(from)
  def parse = 
    ParsedHtml(src.map{ _.replaceAll("""<.+?>|\t""", "") }.filter{ _.nonEmpty })
}

case class ParsedHtml(src:Iterable[String]) extends ScrapedHtml{
  def newTo(from:List[String]) = ParsedHtml(from)
  def parse:ScrapedHtml = this
}

object HtmlScraper {

  def apply(url:String):ScrapedHtml =  RawHtml(getSource(url).getLines.toSeq)

  def getSource(url: String ) = {
    val in = new URL(url).openStream
    val buf = Stream.continually{ in.read }.takeWhile{ -1 != }.map{ _.byteValue}.toArray

    implicit val codec = {
      val Charset = """.*content.*charset\s*=\s*([0-9a-z|\-|_]+).*""".r
      val pf:PartialFunction[String, Codec] = { case Charset(cs) => cs }
      Source.fromBytes(buf,"ISO-8859-1").getLines.find{ 
        pf.isDefinedAt }.collect{ pf }.getOrElse{ Codec.default }
    }
    Source.fromBytes(buf)
  }

  def download(url:String, fileName:String, toParse:Boolean = true ):Unit = {
    val html = if(toParse) HtmlScraper(url).parse else HtmlScraper(url)
    html.write(fileName)
  }
}