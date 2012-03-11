package scalex
package search

import model._
import index.Def

case class SigSearch(sigIndex: SigIndex, sig: NormalizedTypeSig) {

  type Sig = String
  case class Filter(f: Token ⇒ Boolean)

  def search: Fragment = sigFragment(sig.toString.toLowerCase)

  def sigFragment(sig: Sig): Fragment = {

    def scoredSigsToFragment(scoredSigs: List[(Set[Token], Score)]): Fragment = {
      for {
        sigsAndScore ← scoredSigs
        (sigs, score) = sigsAndScore
        sig ← sigs.toList
        d ← sigIndex(sig)
      } yield d -> score
    } toMap

    val indexSigs = sigIndex.keySet

    def sigSigs(
      filters: List[(Sig ⇒ Boolean, Score)],
      exceptSigs: Set[Sig] = Set.empty): List[(Set[Sig], Score)] = {

      def filterSigs(f: Sig ⇒ Boolean) = (indexSigs filter f) diff exceptSigs

      filters match {
        case Nil               ⇒ Nil
        case (f, score) :: Nil ⇒ (filterSigs(f) -> score) :: Nil
        case (f, score) :: rest ⇒ {
          val foundSigs = filterSigs(f)
          val restSigs = sigSigs(rest, exceptSigs ++ foundSigs)
          (foundSigs -> score) :: restSigs
        }
      }
    }

    scoredSigsToFragment(sigSigs(List(
      Filter(_ == sig).f -> 7,
      Filter(_ startsWith sig).f -> 3,
      Filter(_ contains sig).f -> 2
    )))
  }
}
