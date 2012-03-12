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
      case "search" ⇒ search(args mkString " ", 5)
      case "all"    ⇒ search(args mkString " ", 10000)
      case command  ⇒ "Unknown command " + command
    }

    def search(query: String, nb: Int): String =
      (env.engine find RawQuery(query, 1, nb)).fold(identity, {
        case Results(paginator, defs) ⇒
          "%d results for %s\n\n%s\n" format (paginator.nbResults, query, render(defs))
      })

    def dump(fs: List[String]): String = fs match {
      case pack :: file :: sourceBase :: Nil ⇒
        val sources = (new Locator) locate List(new File(file)) map (_.getPath)
        env.dumper.process(pack, sources, sourceBase)
        "Dump complete"
      case _ ⇒ "Usage: dump package /path/to/file /src/source/base"
    }

    def index(): String = {
      env.indexRepo write (env.defRepo.findAll map (_.toIndex))
      "Index complete"
    }

    private def render(d: Def): String =
      "[" + d.pack + "] " + d.name + "\n  " + d.toString + "\n  " + (d.source map (_.url) getOrElse "Unknown source")

    private def render(ds: Seq[Def]): String =
      ds map render map ("* "+) mkString "\n\n"
  }
}
