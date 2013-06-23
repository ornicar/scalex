import sbt._, Keys._

import com.github.retronym.SbtOneJar
import org.scalex.sbt_plugin.ScalexSbtPlugin

trait Resolvers {
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val typesafeS = "typesafe.com snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
  val sonatypeS = "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
  val mandubian = "Mandubian snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"
}

trait Dependencies {
  val compiler = "org.scala-lang" % "scala-compiler" % "2.11.0-M3"
  val scalaz = "org.scalaz" % "scalaz-core_2.10" % "7.0.0"
  val scalazContrib = "org.typelevel" % "scalaz-contrib-210_2.10" % "0.1.4"
  val config = "com.typesafe" % "config" % "1.0.1"
  val scopt = "com.github.scopt" % "scopt_2.10" % "3.0.0"
  val sbinary = "org.scala-tools.sbinary" % "sbinary_2.11" % "0.4.1-THIB3"
  val scalastic = "scalastic" % "scalastic_2.11" % "0.90.0-THIB2"
  val semver = "me.lessis" % "semverfi_2.10" % "0.1.3"
  object akka {
    val version = "2.2.0-RC1"
    val actor = "com.typesafe.akka" % "akka-actor_2.11.0-M3" % version
  }
  object play {
    val version = "2.2-SNAPSHOT"
    val json = "play" % "play-json_2.10" % version
  }
}

object ScalexBuild extends Build with Resolvers with Dependencies {

  private val buildSettings = Defaults.defaultSettings ++ Seq(
    offline := true,
    organization := "org.scalex",
    name := "scalex",
    version := "3.0-SNAPSHOT",
    scalaVersion := "2.11.0-M3",
    libraryDependencies := Seq(config),
    // libraryDependencies in test := Seq(specs2),
    sources in doc in Compile := List(),
    resolvers := Seq(typesafe, typesafeS, sonatype, sonatypeS, iliaz, mandubian),
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:_"),
    publishTo := Some(Resolver.sftp(
      "iliaz",
      "scala.iliaz.com"
    ) as ("scala_iliaz_com", Path.userHome / ".ssh" / "id_rsa"))
  ) ++ SbtOneJar.oneJarSettings ++ ScalexSbtPlugin.defaultSettings

  lazy val scalex = Project("scalex", file("."), settings = buildSettings).settings(
    libraryDependencies ++= Seq(
      compiler, config, scalaz, scalazContrib, semver,
      scopt, sbinary, scalastic, akka.actor, play.json)
  )
}
