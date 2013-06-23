package org.scalex
package cli

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.tools.nsc
import scala.util.{ Try, Success, Failure }

import com.typesafe.config.ConfigFactory

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

  private def process(args: Array[String]): Future[Unit] = (args.toList match {
    case "index" :: name :: version :: rest ⇒ Future {
      index Indexer api.Index(name, version, rest)
    }
    case _ ⇒ Parser.parse(args).fold(Future.failed[Any](badArg(args mkString " "))) {
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer indexConfig)
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer api.Index.test)
      case Config(None, Some(searchConfig)) ⇒
        Env.using(Env.defaultConfig) { env ⇒
          (new search.Search(env) apply searchConfig.expression) andThen {
            case Success(results) ⇒ results.fold(println)(println)
            case Failure(e)       ⇒ println("Is the request valid? " + e)
          }
        }
      case c ⇒ Future.failed(badArg(c.toString))
    }
  }).void
}
