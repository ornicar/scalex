package ophir

import ophir.dump.Dumper
import ophir.dump.Locator
import ophir.db.DefRepo
import ophir.search.Search
import java.io.File

object Cli {

  def dumper = new Dumper

  def locator = new Locator

  def searchEngine = new Search

  def main(args: Array[String]): Unit = sys exit {
    println(args.head match {
      case "dump" => dump(args.toList.tail)
      case "list" => list
      case "search" => search(args.toList.tail mkString " ")
      case command => "Unknown command " + command
    })
    0
  }

  def list: String =
    (DefRepo.findAll map (_.toString)) mkString "\n"

  def search(query: String): String = {
    val results = (searchEngine find query map (_.toString)).toList
    (results mkString "\n") + "\nFound %d results" format results.size
  }

  def dump(fs: List[String]): String = {
    val files = fs map (f => new File(f))
    val sources = locator locate files map (_.getPath)
    dumper.process(sources)
    "Dump complete"
    //list
  }
}
