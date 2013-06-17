package ornicar.scalex
package cli

import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.tools.nsc
import scala.util.{ Try, Success, Failure }

object Main {

  def main(args: Array[String]): Unit = sys exit {
    Await.result(process(args) map (_ ⇒ 0) recover {
      case e: IllegalArgumentException ⇒ {
        println("! %s: %s".format("Illegal argument", e.getMessage))
        1
      }
      case e: Exception ⇒ {
        println("! " + e)
        1
      }
    }, 1 hour)
  }

  private def process(args: Array[String]): Future[Unit] = args.toList match {
    case "index" :: name :: version :: rest ⇒ Future {
      index Indexer api.Index(name, version, rest)
    }
    case _ ⇒ Parser.parse(args).fold(Future.failed[Unit](badArg(args mkString " "))) {
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer indexConfig)
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer api.Index.test)
      case Config(None, Some(searchConfig)) ⇒
        search.Search(ConfigFactory.load) flatMap { searcher ⇒
          searcher(searchConfig.expression) match {
            case Failure(e)   ⇒ Future.failed(e)
            case Success(res) ⇒ Future.successful(renderResult(res))
          }
        }
    }
    case c ⇒ Future.failed(badArg(c.toString))
  }

  private def renderResult(results: List[search.Result]) {
    println(results mkString "\n")
  }
}
