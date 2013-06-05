package scalex.http

final class Timer {

  val start: Long = System.currentTimeMillis

  def currentTime = System.currentTimeMillis - start

  override def toString = "%d ms".format(currentTime)
}

object Timer {

  def monitor[A](op: ⇒ A): (A, Long) = {
    val timer = new Timer
    val result = op
    (result, timer.currentTime)
  }
}
