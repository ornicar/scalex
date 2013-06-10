package scalex
package search

trait IndexSearch[Key <: String, Val] {

  type Fragment = Map[Val, Score]

  val keyIndex: Map[Key, List[Val]]

  lazy val indexKeys: Set[Key] = keyIndex.keySet

  case class Filter(f: Key ⇒ Boolean)

  def search: Fragment

  def fragment(key: Key): Fragment = {

    def scoredKeysToFragment(scoredKeys: List[(Set[Key], Score)]): Fragment = {
      for {
        keysAndScore ← scoredKeys
        (keys, score) = keysAndScore
        key ← keys.toList
        value ← keyIndex(key)
      } yield value -> score
    } toMap

    def scoredKeys(
      filters: List[(Key ⇒ Boolean, Score)],
      exceptKey: Set[Key] = Set.empty): List[(Set[Key], Score)] = {

      def filterKey(f: Key ⇒ Boolean) = (indexKeys filter f) diff exceptKey

      filters match {
        case Nil               ⇒ Nil
        case (f, score) :: Nil ⇒ (filterKey(f) -> score) :: Nil
        case (f, score) :: rest ⇒ {
          val foundKey = filterKey(f)
          val restKey = scoredKeys(rest, exceptKey ++ foundKey)
          (foundKey -> score) :: restKey
        }
      }
    }

    scoredKeysToFragment(scoredKeys(List(
      Filter(_ == key).f -> 7,
      Filter(_ startsWith key).f -> 3,
      Filter(_ contains key).f -> 2
    )))
  }
}
