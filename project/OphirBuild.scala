import sbt._
import Keys._

object OphirBuild extends Build
{
  lazy val root = Project("root", file(".")) settings(
    name := "Ophir",
    version := "0.1",
    scalaVersion := "2.9.1",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % "2.9.1",
      "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
      "org.slf4j" % "slf4j-simple" % "1.6.1" % "runtime",
      "com.novus" %% "salat-core" % "0.0.8-1",
      "org.scala-tools.testing" %% "scalacheck" % "1.9",
      "org.scala-tools.testing" % "test-interface" % "0.5",
      "org.scalatest" % "scalatest_2.9.0" % "1.6.1"
    ),
    resolvers ++= Seq(
        "repo.novus rels" at "http://repo.novus.com/releases/",
        "repo.novus snaps" at "http://repo.novus.com/snapshots/"
    )
  )

  // append -deprecation to the options passed to the Scala compiler
  scalacOptions += "-deprecation"

  scalacOptions += "-unchecked"
}
