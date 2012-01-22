package scalex
package es

import model._

object Mapper extends RecursiveMapper {

  def defToJson(d: Def) = json(d.toMap)

  def defToId(d: Def) = d.id
}
