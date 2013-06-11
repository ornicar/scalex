package scalex
package cli

import java.io.File

sealed trait Mode
case object Update extends Mode

case class Config(
  mode: Option[Mode] = none,
  dir: Option[File] = none)
