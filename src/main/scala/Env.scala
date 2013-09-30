package org.scalex

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

  def using[A](config: ⇒ Config)(f: Env ⇒ Fu[A]): Fu[A] = {
    println("Scalex env starting")
    val env = apply(config)
    f(env) andThen { case _ ⇒ env.shutdown }
  }
}
