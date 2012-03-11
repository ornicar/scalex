package scalex.benchmark

import scalex._
import annotation.tailrec
import com.google.caliper.Param
import scalaz.{ Success, Failure }

// a caliper benchmark is a class that extends com.google.caliper.Benchmark
// the SimpleScalaBenchmark trait does it and also adds some convenience functionality
class Benchmark extends SimpleScalaBenchmark {

  @Param(Array("10")) val length: Int = 0

  var engine: search.Engine = _

  override def setUp() {
    val env = new Env(Map(
      "index.file" -> "/home/thib/scalex/index.dat"
    ))
    engine = env.engine
  }

  def searchString(queryString: String): search.Results =
    engine find search.RawQuery(queryString, 1, 20) match {
      case Failure(e) => sys.error(e)
      case Success(a) => a
    }

  //def timeSearchList(reps: Int) = tSearch(reps, "list")

  def timeSearchListMap(reps: Int) = tSearch(reps, "list map")

  def timeSearchListMapSig(reps: Int) = tSearch(reps, "list[r] => (r => c) => list[c]")

  //def timeSearchBitSetWithFilter(reps: Int) = tSearch(reps, "bitset withFilter")

  //def timeSearchConvoluted(reps: Int) = tSearch(reps, "immutable option collection flatMap")

  //def timeSearchTrollitoErgoSum(reps: Int) = tSearch(reps, "trollito ergo sum")

  private def tSearch(reps: Int, str: String) = repeat(reps) { searchString("list") }
}
