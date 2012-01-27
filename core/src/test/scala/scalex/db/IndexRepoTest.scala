package scalex.test
package db

import scalex.index.Def
import scalex.db.IndexRepo

class IndexRepoTest extends ScalexSpec with Fixtures {

  val tmpFile = "/tmp/scalex_test_index_repo.dat"

  def makeRepo = new IndexRepo(tmpFile + "." + math.abs(util.Random.nextInt))

  "The index repo" should {
    "Deal with empty index" in {
      val repo = makeRepo
      "Write and read" in {
        repo write List[Def]()
        repo.read must beEmpty
      }
    }
    "Deal with non empty index" in {
      val repo = makeRepo
      val defs = List(ind1)
      "Write and read" in {
        repo write defs
        repo.read mustEqual defs
      }
    }
  }
}
