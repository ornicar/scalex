package ophir

import ophir.dump.{Dumper, Locator}
import ophir.db.DefRepo
import ophir.model.Def
import ophir.signature.Signature
import ophir.search.Search
import java.io.File

object Cli {

  def dumper = new Dumper

  def locator = new Locator

  def main(args: Array[String]): Unit = sys exit {
    println(args.head match {
      case "dump" => dump(args.toList.tail)
      case "list" => list
      case "search" => search(args.toList.tail mkString " ")
      case command => "Unknown command " + command
    })
    0
  }

  def list: String = render(DefRepo.findAll.toList)

  def search(query: String): String = (Search find query).right map (_.toList) match {
    case Left(msg) => msg
    case Right(results) =>
      "%d results for %s\n\n%s" format (results.length, query, render(results))
  }

  private def render(d: Def): String =
    d.toString + (
      if (d.comment != "") "\n  " + renderComment(d.comment)
      else ""
    )

  private def render(ds: List[Def]): String =
    ds map render map ("* "+) mkString "\n\n"

  private def renderComment(c: String): String =
    TextUtil.removeTrailingNewline(TextUtil.htmlToText(c))

  private object TextUtil {

    def htmlToText(html: String): String =
      scala.xml.parsing.XhtmlParser(
        scala.io.Source.fromString("<span>"+html.trim+"</span>")
      ).text

    def removeTrailingNewline(text: String): String =
      text.replaceAll("""\n$""", "")
  }

  def dump(fs: List[String]): String = {
    val files = fs map (f => new File(f))
    val sources = locator locate files map (_.getPath)
    dumper.process(sources)
    "Dump complete"
    //list
  }
}
