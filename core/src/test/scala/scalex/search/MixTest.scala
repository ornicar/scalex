package scalex
package search

import search._

import scalaz.{ Success, Failure }

class MixTest extends ScalexTest with WithSearch {

  ": a" in {
    ": string" in {
      ": string" finds "scala.sys.Prop#toString"
    }
  }
  ": a => b" in {
    ": string => int" in {
      ": string => int" finds immutable + "StringOps#size"
    }
  }
  "xxx : a => b" in {
    "size : string => int" in {
      "size : string => int" finds immutable + "StringOps#size"
    }
  }
  "list map" in {
    "list map : list[r] => (r => s) => list[s]" in {
      "list map : list[r] => (r => s) => list[s]" finds immutable + "List#map"
    }
  }
}
