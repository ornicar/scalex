package scalex

import scalex.dump.Locator
import scalex.model.Def
import scalex.search._

import java.io.File
import scalaz.Scalaz.{ success, failure }

object Cli {

  def main(args: Array[String]): Unit = sys exit {
    println(args.toList match {
      case config :: command :: args ⇒ {
        val env = Env(config)
        EnvCli(env)(command, args)
      }
      case _ ⇒ "Usage: run /path/to/scalex.conf command args"
    })
    0
  }

  case class EnvCli(env: Env) {

    def apply(command: String, args: List[String]) = command match {
      case "dump"   ⇒ dump(args)
      case "index"  ⇒ index()
      case "search" ⇒ search(args mkString " ", 3)
      case "all"    ⇒ search(args mkString " ", 10000)
      case command  ⇒ "Unknown command " + command
    }

    def search(query: String, nb: Int): String =
      (env.engine find RawQuery(query, 1, nb)).fold(identity, {
        case Results(paginator, defs) ⇒
          "%d results for %s\n\n%s\n" format (paginator.nbResults, query, render(defs))
      })

    def dump(fs: List[String]): String = {
      val pack = fs.head
      val files = fs.tail map (f ⇒ new File(f))
      val sources = (new Locator) locate files map (_.getPath)
      env.dumper.process(pack, sources)
      "Dump complete"
    }

    def index(): String = {
      env.indexRepo write (env.defRepo.findAll map (_.toIndex))
      "Index complete"
    }

    private def render(d: Def): String =
      "[" + d.pack + "] " + d.name + "\n  " + d.toString + "\n  " + d.encodedDocUrl

    private def render(ds: Seq[Def]): String =
      ds map render map ("* "+) mkString "\n\n"
  }
}
