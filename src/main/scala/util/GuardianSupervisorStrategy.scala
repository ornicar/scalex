package org.scalex
package util

import akka.actor._
import akka.actor.SupervisorStrategy._

class GuardianSupervisorStrategy extends SupervisorStrategyConfigurator {

  def create(): SupervisorStrategy =
    OneForOneStrategy()({
      // changed from Stop
      case _: ActorInitializationException ⇒ Escalate
      case _: ActorKilledException         ⇒ Stop
      case _: Exception                    ⇒ Restart
    })

}
