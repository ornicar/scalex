package org.scalex
package server

import scala.util.{ Try, Success, Failure }
import scala.concurrent.Await
import scala.concurrent.duration._

import tiscaf._

final class Server(searcher: search.Search, port: Int) extends HServer {

  val apps = Seq(new ScalexApp(searcher))
  val ports = Set(port)
  override protected val name = "scalex"
  // do not start the stop thread
  override protected def startStopListener {}

  start
  println("press enter to stop...")
  Console.readLine
  stop
}

private[server] final class ScalexApp(searcher: search.Search) extends HApp {

  override def keepAlive = false
  override def gzip = false
  override def chunked = false
  override def tracking = HTracking.NotAllowed

  val timeout = 1.second

  searcher("Preload search engine") onComplete {
    case Success(_)   ⇒ println("Search engine loaded")
    case Failure(err) ⇒ println(s"Failure while preloading: $err")
  }

  def resolve(req: HReqData): Option[HLet] = Some(new HSimpleLet {

    def act(talk: HTalk) {

      def write(status: HStatus.type ⇒ HStatus.Value)(str: String) = {
        val bytes = str getBytes "UTF-8"
        talk
          .setContentType("text/plain; charset=UTF-8")
          .setCharacterEncoding("UTF-8")
          .setContentLength(bytes.size)
          .setStatus(status(HStatus))
          .write(bytes)
      }

      talk.req param "q" match {
        case None ⇒ write(_.BadRequest)("Empty query")
        case Some(q) ⇒ Try(Await.result(searcher(q), timeout)) match {
          case Success(res) => res.fold(
            err ⇒ write(_.BadRequest)(err),
            res ⇒ write(_.OK)(res.toString)
          )
          case Failure(err) ⇒ write(_.InternalServerError) {
            s"""Failure while searching for "$q": $err""".pp
          }
        }
      }
    }
  })
}
