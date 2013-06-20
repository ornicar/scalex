package org

import scalaz._

package object scalex
    extends instances
    with StateFunctions // Functions related to the state monad
    with syntax.ToTypeClassOps // syntax associated with type classes
    with syntax.ToDataOps // syntax associated with Scalaz data structures
    with std.AllInstances // Type class instances for the standard library types
    with std.AllFunctions // Functions related to standard library types
    with syntax.std.ToAllStdOps // syntax associated with standard library types
    with IdInstances // Identity type and instances
    with contrib.std.TryInstances
    with contrib.std.FutureInstances {

  type File = java.io.File

  implicit def execontext = scala.concurrent.ExecutionContext.Implicits.global

  def badArg(msg: String) = new IllegalArgumentException(msg)

  object makeTimeout {

    import akka.util.Timeout
    import scala.concurrent.duration._

    implicit val short = seconds(1)
    implicit val large = seconds(5)
    implicit val veryLarge = minutes(10)

    def apply(duration: FiniteDuration) = Timeout(duration)
    def seconds(s: Int): Timeout = Timeout(s.seconds)
    def minutes(m: Int): Timeout = Timeout(m.minutes)
  }
}
