package org.scalex

import akka.actor.{ Actor, ActorSystem, Props }
import akka.kernel.Bootable

final class ScalexKernel extends Bootable {

  val system = ActorSystem("scalex")

  def startup = {
    println("scalex startup")
  }

  def shutdown = {
    system.shutdown()
  }
}
