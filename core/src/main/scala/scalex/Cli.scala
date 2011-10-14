package scalex

import scalex.dump.{Dumper, Locator}
import scalex.db.DefRepo
import scalex.model.Def
import scalex.signature.Signature
import scalex.search.{Search, Query}
import java.io.File

object Cli {

  def dumper = new Dumper

  def locator = new Locator

  def main(args: Array[String]): Unit = sys exit {
    println(args.head match {
      case "dump" => dump(args.toList.tail)
      case "search" => search(args.toList.tail mkString " ")
      case command => "Unknown command " + command
    })
    0
  }

  def search(query: String): String = (Search find Query(query, 1, 5)) match {
    case Left(msg) => msg
    case Right(paginator) =>
      "%d results for %s\n\n%s" format (paginator.nbResults, query, render(paginator.currentPageResults))
  }

  private def render(d: Def): String =
    "[" + d.pack + "] " + d.name + "\n  " + d.toString + "\n" + (
      d.comment map (_.short) getOrElse "no comment"
    )

  private def render(ds: Seq[Def]): String =
    ds map render map ("* "+) mkString "\n\n"

  def dump(fs: List[String]): String = {
    val pack = fs.head
    val files = fs.tail map (f => new File(f))
    val sources = locator locate files map (_.getPath)
    dumper.process(pack, sources)
    "Dump complete"
  }
}
