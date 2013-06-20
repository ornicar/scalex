package org.scalex
package util

import scala.concurrent.Future

private[scalex] final class Timer {

  val start: Long = System.currentTimeMillis

  def currentTime = System.currentTimeMillis - start

  override def toString = Timer show currentTime
}

private[scalex] object Timer {

  def printAndMonitorFuture[A](msg: ⇒ String)(op: ⇒ Future[A]): Future[A] = {
    print(msg)
    monitorFuture(op) map {
      case (res, time) ⇒ {
        println(" - " + show(time))
        res
      }
    }
  }

  def monitorFuture[A](op: ⇒ Future[A]): Future[(A, Long)] = {
    val timer = new Timer
    op map { _ -> timer.currentTime }
  }

  def printAndMonitor[A](msg: ⇒ String)(op: ⇒ A): A = {
    print(msg)
    monitor(op) match {
      case (res, time) ⇒ {
        println(" - " + show(time))
        res
      }
    }
  }

  def monitor[A](op: ⇒ A): (A, Long) = {
    val timer = new Timer
    val result = op
    (result, timer.currentTime)
  }

  def show(time: Long) = time + " ms"
}
