import sbt._
import Keys._

trait BuildSettings {
  val buildOrganization = "com.github.ornicar"
  val buildVersion = "0.2"
  val buildScalaVersion = "2.9.1"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    shellPrompt := ShellPrompt.buildShellPrompt,
    scalacOptions := Seq("-deprecation", "-unchecked"))
}

trait Resolvers {
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
  val novus = "repo.novus snaps" at "http://repo.novus.com/snapshots/"
  val sonatype = "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/"
}

trait Dependencies {
  val specs2 = "org.specs2" %% "specs2" % "1.7.1"
  val casbah = "com.mongodb.casbah" %% "casbah" % "2.1.5-1"
  val salat = "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
  val compiler = "org.scala-lang" % "scala-compiler" % "2.9.1"
  val slf4jNop = "org.slf4j" % "slf4j-nop" % "1.6.4"
  val paginator = "com.github.ornicar" %% "paginator-core" % "1.4"
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.4"
  val scalatra = "org.scalatra" %% "scalatra" % "2.0.3"
  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  val jetty = "org.eclipse.jetty" % "jetty-webapp" % "8.0.4.v20111024" % "container"
  val hasher = "com.roundeights" % "hasher" % "0.3" from "http://cloud.github.com/downloads/Nycto/Hasher/hasher_2.9.1-0.3.jar"
  val sbinary = "org.scala-tools.sbinary" %% "sbinary" % "0.4.1-SNAPSHOT"
}

object ScalexBuild extends Build with BuildSettings with Resolvers with Dependencies {
  lazy val core = Project("core", file("core"),
    settings = buildSettings ++ Seq(
      name := "scalex-core",
      resolvers := Seq(typesafe, iliaz, novus, sonatype),
      libraryDependencies ++= Seq(specs2, casbah, salat, compiler, paginator, scalaz, hasher, sbinary, slf4jNop)))

  lazy val http = Project("http", file("http"),
    settings = buildSettings ++ Seq(
      name := "scalex-http",
      resolvers := Seq(novus, iliaz),
      libraryDependencies ++= Seq(scalatra, servlet, jetty, slf4jNop))) dependsOn "core"
}

object ShellPrompt {
  val buildShellPrompt =
    (state: State) ⇒ "scalex:%s> ".format(Project.extract(state).currentProject.id)
}
