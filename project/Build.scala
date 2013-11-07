import sbt._, Keys._

// import org.scalex.sbt_plugin.ScalexSbtPlugin
import com.github.retronym.SbtOneJar

trait Resolvers {
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val typesafeS = "typesafe.com snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
  val sonatypeS = "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
  val mandubian = "Mandubian snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"
}

trait Dependencies {
  val compiler = "org.scala-lang" % "scala-compiler" % "2.11.0-M5"
  val scalaz = "org.scalaz" % "scalaz-core_2.10" % "7.0.4"
  val scalazContrib = "org.typelevel" % "scalaz-contrib-210_2.10" % "0.1.5"
  val config = "com.typesafe" % "config" % "1.0.2"
  val scopt = "com.github.scopt" % "scopt_2.10" % "3.1.0"
  val sbinary = "org.scala-tools.sbinary" % "sbinary_2.10" % "0.4.2"
  val semver = "me.lessis" % "semverfi_2.10" % "0.1.3"
  val elastic4s = "com.sksamuel.elastic4s" % "elastic4s_2.10" % "0.90.5.5"
  object akka {
    val version = "2.2.1"
    val actor = "com.typesafe.akka" %% "akka-actor" % version
  }
  object play {
    val version = "2.2-SNAPSHOT"
    val json = "play" % "play-json_2.10" % version
  }
  object apache {
    val io = "commons-io" % "commons-io" % "2.4"
  }
  val specs2 = "org.specs2" % "specs2_2.10" % "2.3.1" % "test"
}

object ScalexBuild extends Build with Resolvers with Dependencies {

  private val buildSettings = Defaults.defaultSettings ++ Seq(
    offline := false,
    organization := "org.scalex",
    name := "scalex",
    version := "3.0-SNAPSHOT",
    scalaVersion := "2.11.0-M5",
    libraryDependencies := Seq(config),
    // libraryDependencies in test := Seq(specs2),
    sources in doc in Compile := List(),
    resolvers := Seq(typesafe, typesafeS, sonatype, sonatypeS, iliaz, mandubian),
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:_"),
    publishTo := Some(Resolver.sftp(
      "iliaz",
      "scala.iliaz.com"
    ) as ("scala_iliaz_com", Path.userHome / ".ssh" / "id_rsa"))
  ) ++ SbtOneJar.oneJarSettings
  // ++ ScalexSbtPlugin.defaultSettings

  lazy val scalex = Project("scalex", file("."), settings = buildSettings).settings(
    libraryDependencies ++= Seq(
      compiler, config, scalaz, scalazContrib, semver,
      scopt, sbinary, elastic4s, akka.actor, play.json,
      apache.io, specs2)
  )
}
