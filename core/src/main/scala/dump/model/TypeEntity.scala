package scalex.dump.model

import scala.tools.nsc.doc.model.{TypeEntity => NscTypeEntity, TemplateEntity}
import scala.collection._


/** A type. Note that types and templates contain the same information only for the simplest types. For example, a type
  * defines how a template's type parameters are instantiated (as in `List[Cow]`), what the template's prefix is
  * (as in `johnsFarm.Cow`), and supports compound or structural types. */
abstract class TypeEntity extends NscTypeEntity {

  /** The human-readable representation of this type. */
  def name: String

  /** Maps which parts of this type's name reference entities. The map is indexed by the position of the first
    * character that reference some entity, and contains the entity and the position of the last referenced
    * character. The referenced character ranges do not to overlap or nest. The map is sorted by position. */
  def refEntity: SortedMap[Int, (TemplateEntity, Int)]

  def fullType: scalex.model.TypeEntity

  /** The human-readable representation of this type. */
  override def toString = name

}

object TypeEntity {

  def apply(te: NscTypeEntity, ft: scalex.model.TypeEntity): TypeEntity = new TypeEntity {
    val name = te.name
    val refEntity = te.refEntity
    val fullType = ft
  }
}
