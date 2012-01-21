import sbt._
import Keys._

object Resolvers {
  val twitter = "twitter.com" at "http://maven.twttr.com/"
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
  val novus = "novus" at "http://repo.novus.com/snapshots/"
}

object ScalexBuild extends Build
{
  import Resolvers._

  lazy val core = Project("core", file("core")) settings(
    name := "Scalex Core",
    version := "0.1",
    scalaVersion := "2.9.1",
    scalacOptions += "-deprecation",
    scalacOptions += "-unchecked",
    shellPrompt := ShellPrompt.buildShellPrompt,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % "2.9.1",
      "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
      "org.slf4j" % "slf4j-simple" % "1.6.1" % "runtime",
      "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
      "org.scala-tools.testing" %% "scalacheck" % "1.9",
      "org.scala-tools.testing" % "test-interface" % "0.5",
      "org.scalatest" % "scalatest_2.9.0" % "1.6.1",
      "com.github.ornicar" %% "paginator-core" % "1.2",
      "com.github.ornicar" %% "paginator-salat-adapter" % "1.1",
      "org.scalaz" %% "scalaz-core" % "6.0.3"
    ),
    resolvers ++= Seq(novus, iliaz)
  )

  lazy val http = Project("http", file("http")) dependsOn(core) settings(
    name := "Scalex HTTP",
    version := "0.1",
    scalaVersion := "2.9.1",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % "2.0.1",
      "javax.servlet" % "servlet-api" % "2.5" % "provided",
      "org.eclipse.jetty" % "jetty-webapp" % "8.0.4.v20111024" % "container"
    ),
    resolvers ++= Seq(novus)
  )
}

object ShellPrompt {

  val buildShellPrompt = {
    (state: State) =>
      {
        val currProject = Project.extract(state).currentProject.id
        "hermes:%s> ".format(currProject)
      }
  }
}
