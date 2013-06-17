package ornicar.scalex
package index

import scala.tools.nsc.doc.Universe

import model._

private[index] object Universer {

  def apply(universe: Universe): List[DocTemplate] = {
    new Mapper().docTemplate(universe.rootPackage).templates
  }
}
