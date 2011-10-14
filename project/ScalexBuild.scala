import sbt._
import Keys._

object ScalexBuild extends Build
{
  lazy val core = Project("core", file("core")) settings(
    name := "Scalex Core",
    version := "0.1",
    scalaVersion := "2.9.1",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % "2.9.1",
      "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
      "org.slf4j" % "slf4j-simple" % "1.6.1" % "runtime",
      "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
      "org.scala-tools.testing" %% "scalacheck" % "1.9",
      "org.scala-tools.testing" % "test-interface" % "0.5",
      "org.scalatest" % "scalatest_2.9.0" % "1.6.1",
      "com.github.ornicar" % "paginator-core_2.9.1" % "1.0",
      "com.github.ornicar" % "paginator-salat-adapter_2.9.1" % "1.0"
    ),
    resolvers ++= Seq(
      "repo.novus rels" at "http://repo.novus.com/releases/",
      "repo.novus snaps" at "http://repo.novus.com/snapshots/"
    )
  )

  lazy val http = Project("http", file("http")) dependsOn(core) settings(
    name := "Scalex HTTP",
    version := "0.1",
    scalaVersion := "2.9.1",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % "2.0.1",
      "javax.servlet" % "servlet-api" % "2.5" % "provided",
      "org.eclipse.jetty" % "jetty-webapp" % "7.4.5.v20110725" % "jetty"
    )
  )

  // append -deprecation to the options passed to the Scala compiler
  scalacOptions += "-deprecation"

  scalacOptions += "-unchecked"
}
