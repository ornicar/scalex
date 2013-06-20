package org.scalex

import akka.actor.ActorSystem
import com.typesafe.config.{ Config, ConfigFactory }

case class Env(

  // typesafe config object
  config: Config,

  // akka actor system, or "context"
  system: ActorSystem)

object Env {

  // zero parameters env instanciation
  def default = {
    val config = ConfigFactory.load.getConfig("scalex")
    Env(
      config = config,
      system = ActorSystem("Scalex", config)
    )
  }
}
