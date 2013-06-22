package org.scalex

import scala.concurrent.Future

import akka.actor.ActorSystem
import com.typesafe.config.{ Config, ConfigFactory }

case class Env(

    // typesafe config object
    config: Config,

    // akka actor system, or "context"
    system: ActorSystem) {

  def shutdown { system.shutdown }
}

object Env {

  def apply(config: Config): Env = Env(
    config = config,
    system = ActorSystem("scalex", config))

  def defaultConfig = ConfigFactory.load.getConfig("scalex")

  def using[A](config: ⇒ Config)(f: Env ⇒ Future[A]): Future[A] = {
    println("Scalex env starting")
    val env = apply(config)
    val res = f(env)
    res onComplete {
      case _ ⇒ {
        env.shutdown
        println("Scalex env shut down.")
      }
    }
    res
  }
}
