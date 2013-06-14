package ornicar.scalex
package model

/** A template (class, trait, object or package) for which documentation is available. Only templates for which
  * a source file is given are documented by Scaladoc. */
case class DocTemplate(

  /** a Template is a MemberTemplate */
  memberTemplate: MemberTemplate,

  /** The source file in which the current template is defined and the line where the definition starts, if they exist.
    * A source file exists for all templates, except for those that are generated synthetically by Scaladoc. */
  inSource: Option[(String, Int)],

  /** An HTTP address at which the source of this template is available, if it is available. An address is available
    * only if the `docsourceurl` setting has been set. */
  sourceUrl: Option[java.net.URL],

  /** All class, trait and object templates which are part of this template's linearization, in lineratization order.
    * This template's linearization contains all of its direct and indirect super-classes and super-traits. */
  // def linearizationTemplates: List[TemplateEntity]

  /** All instantiated types which are part of this template's linearization, in lineratization order.
    * This template's linearization contains all of its direct and indirect super-types. */
  // def linearizationTypes: List[TypeEntity]

  /** All class, trait and object templates for which this template is a *direct* super-class or super-trait.
   *  Only templates for which documentation is available in the universe (`DocTemplateEntity`) are listed. */
  // def directSubClasses: List[DocTemplateEntity]

  /** All members of this template. If this template is a package, only templates for which documentation is available
    * in the universe (`DocTemplateEntity`) are listed. */
  members: List[Member],

  /** All templates that are members of this template. If this template is a package, only templates for which
    * documentation is available  in the universe (`DocTemplateEntity`) are listed. */
  templates: List[DocTemplate]

  /** All methods that are members of this template. */
  // def methods: List[Def]

  /** All values, lazy values and variables that are members of this template. */
  // def values: List[Val]

  /** All abstract types that are members of this template. */
  // def abstractTypes: List[AbstractType]

  /** All type aliases that are members of this template. */
  // def aliasTypes: List[AliasType]

  /** The primary constructor of this class, if it has been defined. */
  // def primaryConstructor: Option[Constructor]

  /** All constructors of this class, including the primary constructor. */
  // def constructors: List[Constructor]

  /** The companion of this template, or none. If a class and an object are defined as a pair of the same name, the
    * other entity of the pair is the companion. */
  // def companion: Option[DocTemplateEntity]

  /** The implicit conversions this template (class or trait, objects and packages are not affected) */
  // def conversions: List[ImplicitConversion]

  /** The shadowing information for the implicitly added members */
  // def implicitsShadowing: Map[MemberEntity, ImplicitMemberShadowing]

  /** Classes that can be implcitly converted to this class */
  // def incomingImplicitlyConvertedClasses: List[(DocTemplateEntity, ImplicitConversion)]

  /** Classes to which this class can be implicitly converted to
      NOTE: Some classes might not be included in the scaladoc run so they will be NoDocTemplateEntities */
  // def outgoingImplicitlyConvertedClasses: List[(TemplateEntity, TypeEntity, ImplicitConversion)]

  /** If this template takes place in inheritance and implicit conversion relations, it will be shown in this diagram */
  // def inheritanceDiagram: Option[Diagram]

  /** If this template contains other templates, such as classes and traits, they will be shown in this diagram */
  // def contentDiagram: Option[Diagram]

  /** Returns the group description taken either from this template or its linearizationTypes */
  // def groupDescription(group: String): Option[Body]

  /** Returns the group description taken either from this template or its linearizationTypes */
  // def groupPriority(group: String): Int

  /** Returns the group description taken either from this template or its linearizationTypes */
  // def groupName(group: String): String
) {

  def template = memberTemplate.template

  def qualifiedName = template.entity.qualifiedName

  // override def toString = qualifiedName
}
