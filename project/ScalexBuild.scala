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
  val sonatype = "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/"
}

trait Dependencies {
  val specs2 = "org.specs2" %% "specs2" % "1.6.1"
  val compiler = "org.scala-lang" % "scala-compiler" % "2.9.1"
  val slf4j = "org.slf4j" % "slf4j-simple" % "1.6.1" % "runtime"
  val paginator = "com.github.ornicar" %% "paginator-core" % "1.2"
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.3"
  val scalatra = "org.scalatra" %% "scalatra" % "2.0.1"
  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  val jetty = "org.eclipse.jetty" % "jetty-webapp" % "8.0.4.v20111024" % "container"
  val elasticsearch = "org.elasticsearch" % "elasticsearch" % "0.18.7"
}

object ScalexBuild extends Build with BuildSettings with Resolvers with Dependencies
{
  //lazy val scalaElasticsearch = Project("elasticsearch", file("scala-elasticsearch"),
    //settings = buildSettings ++ Seq(
      //name := "scala-elasticsearch",
      //resolvers := Seq(sonatype),
      //libraryDependencies ++= Seq(specs2, elasticsearch)))

  lazy val core = Project("core", file("core"),
    settings = buildSettings ++ Seq(
      name := "scalex-core",
      resolvers := Seq(typesafe, iliaz, sonatype),
      libraryDependencies ++= Seq(specs2, compiler, paginator, scalaz, elasticsearch)))

  lazy val http = Project("http", file("http"),
    settings = buildSettings ++ Seq(
      name := "scalex-http",
      resolvers := Seq(),
      libraryDependencies ++= Seq(scalatra, servlet, jetty, slf4j))) dependsOn "core"
}

object ShellPrompt {
  val buildShellPrompt =
    (state: State) => "scalex:%s> ".format(Project.extract(state).currentProject.id)
}
