import sbt._
import Keys._
import play.Project._

trait Resolvers {
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
}

trait Dependencies {
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.4"
  val specs2 = "org.specs2" %% "specs2" % "1.14"
  val guava = "com.google.guava" % "guava" % "14.0.1"
}

object ApplicationBuild extends Build with Resolvers with Dependencies {

  val appName = "scalex"
  val appVersion = "3.0"

  val compilerOptions = Seq("-deprecation", "-unchecked", "-feature", "-language:_")

  val main = play.Project(appName, appVersion).settings(
    resolvers ++= Seq(typesafe, iliaz),
    libraryDependencies ++= Seq(scalaz, guava),
    scalacOptions := compilerOptions) 
}
