import sbt._, Keys._

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
  val scalazContrib = "org.typelevel" % "scalaz-contrib-210_2.10" % "0.1.4"
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
  ) ++ Seq(scalexTask)

  lazy val scalex = Project("scalex", file("."), settings = buildSettings).settings(
    libraryDependencies ++= Seq(compiler, scalaz, scalazContrib, sbinary, scopt)
  )

  def scalexTaskKey = TaskKey[File]("scalex", "Generates scalex database.") 

	import java.io.{File, PrintWriter}
  def scalexTask = scalexTaskKey <<= (
    configuration in Compile, 
    dependencyClasspath in Compile,
    streams in Compile,
    compilers in Compile,
    sources in Compile,
    target in Compile,
    scalacOptions in Compile,
    javacOptions in Compile,
    apiMappings in Compile,
    maxErrors in Compile) map {
    (config, depCP, s, cs, srcs, out, sOpts, jOpts, xapis, maxE) =>
    val hasScala = srcs.exists(_.name.endsWith(".scala"))
    val hasJava = srcs.exists(_.name.endsWith(".java"))
    val cp = Attributed.data(depCP).toList
    val label = Defaults.nameForSrc(config.name)
    val (options, runDoc) =
      if(hasScala)
        (sOpts ++ Opts.doc.externalAPI(xapis), // can't put the .value calls directly here until 2.10.2
          Doc.scaladoc(label, s.cacheDirectory / "scala", cs.scalac.onArgs(exported(s, "scaladoc"))))
      else if(hasJava)
        (jOpts,
          Doc.javadoc(label, s.cacheDirectory / "java", cs.javac.onArgs(exported(s, "javadoc"))))
      else
        (Nil, RawCompileLike.nop)
    runDoc(srcs, cp, out, options, maxE, s.log)
    out
  }

	private[this] def exported(w: PrintWriter, command: String): Seq[String] => Unit = args =>
		w.println( (command +: args).mkString(" ") )
	private[this] def exported(s: TaskStreams, command: String): Seq[String] => Unit = args =>
		exported(s.text("export"), command)
}
