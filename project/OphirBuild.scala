import sbt._
import Keys._

object OphirBuild extends Build
{
  lazy val root = Project("root", file(".")) settings(
    name := "Ophir",
    version := "0.1",
    scalaVersion := "2.9.1",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % "2.9.1"
    )
  )

  // append -deprecation to the options passed to the Scala compiler
  scalacOptions += "-deprecation"

  scalacOptions += "-unchecked"
}
