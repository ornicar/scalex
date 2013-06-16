package ornicar.scalex
package api

case class Index(args: List[String]) {
  def add(arg: String) = copy(args = args :+ arg)
}

object Index {

  def test = Index(
    """-deprecation -unchecked -feature -language:_ -d /home/thib/scalachess/database.scalex -bootclasspath
  /usr/lib/jvm/java-7-openjdk/jre/lib/resources.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/rt.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/sunrsasign.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/jsse.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/jce.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/charsets.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/netx.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/plugin.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/rhino.jar:/usr/lib/jvm/java-7-openjdk/jre/lib/jfr.jar:/usr/lib/jvm/java-7-openjdk/jre/classes:/home/thib/.sbt/boot/scala-2.10.2/lib/scala-library.jar
  -classpath
  /home/thib/.ivy2/cache/org.scalaz/scalaz-core_2.10/jars/scalaz-core_2.10-6.0.4.jar:/home/thib/.ivy2/cache/com.github.ornicar/scalalib_2.10/jars/scalalib_2.10-3.3.jar:/home/thib/.ivy2/cache/org.specs2/specs2_2.10/jars/specs2_2.10-1.14.jar:/home/thib/.ivy2/cache/org.specs2/specs2-scalaz-core_2.10/jars/specs2-scalaz-core_2.10-7.0.0.jar:/home/thib/.ivy2/cache/org.specs2/specs2-scalaz-concurrent_2.10/jars/specs2-scalaz-concurrent_2.10-7.0.0.jar:/home/thib/.ivy2/cache/org.specs2/scalaz-effect_2.10/jars/scalaz-effect_2.10-7.0.0.jar:/home/thib/.ivy2/cache/org.specs2/scalaz-core_2.10/jars/scalaz-core_2.10-7.0.0.jar:/home/thib/.ivy2/cache/joda-time/joda-time/jars/joda-time-2.1.jar:/home/thib/.ivy2/cache/org.joda/joda-convert/jars/joda-convert-1.2.jar:/home/thib/.ivy2/cache/hasher/hasher_2.10/jars/hasher_2.10-0.3.1.jar
  /home/thib/scalachess/src/main/scala/Actor.scala /home/thib/scalachess/src/main/scala/Board.scala
  /home/thib/scalachess/src/main/scala/Clock.scala /home/thib/scalachess/src/main/scala/Color.scala
  /home/thib/scalachess/src/main/scala/EcoDb.scala /home/thib/scalachess/src/main/scala/EloCalculator.scala
  /home/thib/scalachess/src/main/scala/Game.scala /home/thib/scalachess/src/main/scala/History.scala
  /home/thib/scalachess/src/main/scala/Mode.scala /home/thib/scalachess/src/main/scala/Move.scala
  /home/thib/scalachess/src/main/scala/OpeningExplorer.scala /home/thib/scalachess/src/main/scala/Piece.scala
  /home/thib/scalachess/src/main/scala/Pos.scala /home/thib/scalachess/src/main/scala/Replay.scala
  /home/thib/scalachess/src/main/scala/ReverseEngineering.scala /home/thib/scalachess/src/main/scala/Role.scala
  /home/thib/scalachess/src/main/scala/Setup.scala /home/thib/scalachess/src/main/scala/Side.scala
  /home/thib/scalachess/src/main/scala/Situation.scala /home/thib/scalachess/src/main/scala/Speed.scala
  /home/thib/scalachess/src/main/scala/Status.scala /home/thib/scalachess/src/main/scala/Variant.scala
  /home/thib/scalachess/src/main/scala/format/Forsyth.scala /home/thib/scalachess/src/main/scala/format/Nag.scala
  /home/thib/scalachess/src/main/scala/format/UciDump.scala /home/thib/scalachess/src/main/scala/format/UciMove.scala
  /home/thib/scalachess/src/main/scala/format/Visual.scala /home/thib/scalachess/src/main/scala/format/pgn/Dumper.scala
  /home/thib/scalachess/src/main/scala/format/pgn/Parser.scala /home/thib/scalachess/src/main/scala/format/pgn/Reader.scala
  /home/thib/scalachess/src/main/scala/format/pgn/model.scala /home/thib/scalachess/src/main/scala/format/pgn/parsingModel.scala
  /home/thib/scalachess/src/main/scala/format/pgn/tagModel.scala /home/thib/scalachess/src/main/scala/package.scala"""
      .lines.mkString(" ").split(" ").toList.filter(_.nonEmpty))
}

case class Search(words: List[String] = Nil) {
  def add(word: String) = copy(words = words :+ word)
  override def toString = "Search " + (words mkString " ")
}
