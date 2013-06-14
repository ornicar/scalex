package ornicar.scalex
package cli

import api.{ Index, Search }

case class Config(
    index: Option[Index] = none,
    search: Option[Search] = none) {

  def withIndex(f: Index ⇒ Index) = copy(index = index map f)
  def withSearch(f: Search ⇒ Search) = copy(search = search map f)
}
