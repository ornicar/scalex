import sbt._
import Keys._

trait Resolvers {
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val typesafeS = "typesafe.com snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
  val sonatypeS = "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
  val sonatypeP = "sonatype-public" at "https://oss.sonatype.org/content/groups/public"
}

trait Dependencies {
  val compiler = "org.scala-lang" % "scala-compiler" % "2.11.0-M3"
  val scalaz = "org.scalaz" % "scalaz-core_2.10" % "7.0.0"
  val sbinary = "org.scala-tools.sbinary" % "sbinary_2.10" % "0.4.1"
  val scalalib = "com.github.ornicar" % "scalalib_2.10" % "3.3"
  val config = "com.typesafe" % "config" % "1.0.1"
  val scopt = "com.github.scopt" % "scopt_2.10" % "3.0.0"
}

object ScalexBuild extends Build with Resolvers with Dependencies {

  private val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.github.ornicar",
    version := "3.0",
    scalaVersion := "2.11.0-M3",
    libraryDependencies := Seq(config, scalalib),
    // libraryDependencies in test := Seq(specs2),
    resolvers := Seq(typesafe, typesafeS, iliaz, sonatype, sonatypeS, sonatypeP),
    scalacOptions := Seq("-deprecation", "-unchecked", "-feature", "-language:_")  
  )

  lazy val scalex = Project("scalex", file("."), settings = buildSettings).settings(
    libraryDependencies ++= Seq(compiler, scalaz, sbinary, scopt)
  )

  // lazy val core = Project("core", file("core"), settings = buildSettings).settings(
  //   libraryDependencies ++= Seq(compiler, scalaz, sbinary)
  // )
}
