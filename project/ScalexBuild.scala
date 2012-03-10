import sbt._
import Keys._

trait Resolvers {
  val codahale = "repo.codahale.com" at "http://repo.codahale.com/"
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val typesafeS = "typesafe.com snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
  val sonatypeS = "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
  val novusS = "repo.novus snaps" at "http://repo.novus.com/snapshots/"
}

trait Dependencies {
  val specs2 = "org.specs2" %% "specs2" % "1.8.2"
  val casbah = "com.mongodb.casbah" %% "casbah" % "2.1.5-1"
  val salat = "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
  val compiler = "org.scala-lang" % "scala-compiler" % "2.9.1"
  val slf4jNop = "org.slf4j" % "slf4j-nop" % "1.6.4"
  val paginator = "com.github.ornicar" %% "paginator-core" % "1.4"
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.4"
  val hasher = "com.roundeights" % "hasher" % "0.3" from "http://cloud.github.com/downloads/Nycto/Hasher/hasher_2.9.1-0.3.jar"
  val sbinary = "org.scala-tools.sbinary" %% "sbinary" % "0.4.1-SNAPSHOT"
  val scalalib = "com.github.ornicar" %% "scalalib" % "1.21"
}

object ScalexBuild extends Build with Resolvers with Dependencies {

  private val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.github.ornicar",
    version := "1.0",
    scalaVersion := "2.9.1",
    libraryDependencies := Seq(scalalib),
    libraryDependencies in test := Seq(specs2),
    resolvers := Seq(typesafe, iliaz, novusS, sonatype),
    shellPrompt := {
      (state: State) ⇒ "%s> ".format(Project.extract(state).currentProject.id)
    },
    scalacOptions := Seq("-deprecation", "-unchecked")
  )

  lazy val core = Project("core", file("core"), settings = buildSettings).settings(
    libraryDependencies ++= Seq(casbah, salat, compiler, paginator, scalaz, hasher, sbinary, slf4jNop)
  )
}
