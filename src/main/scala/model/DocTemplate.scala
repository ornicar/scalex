package org.scalex
package model

/** A template (class, trait, object or package) for which documentation is available. Only templates for which
  * a source file is given are documented by Scaladoc. */
case class DocTemplate(

  /** a DocTemplate is a Member */
  member: Member,

  /** a DocTemplate is a Template */
  template: Template,

  /** a DocTemplate is a HigherKinded */
  typeParams: List[TypeParam],

  /**
    * The value parameters of this case class, or an empty list if this class is not a case class. As case class value
    * parameters cannot be curried, the outer list has exactly one element.
    */
  valueParams: List[List[ValueParam]],

  /**
    * The direct super-type of this template
    * e.g: {{{class A extends B[C[Int]] with D[E]}}} will have two direct parents: class B and D
    * NOTE: we are dropping the refinement here!
    */
  parentTypes: List[QualifiedName],

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
  templates: List[DocTemplate],

  /** All methods that are members of this template. */
  methods: List[Def],

  /** All values, lazy values and variables that are members of this template. */
  values: List[Val],

  /** All abstract types that are members of this template. */
  abstractTypes: List[AbstractType],

  /** All type aliases that are members of this template. */
  aliasTypes: List[AliasType],

  /** The primary constructor of this class, if it has been defined. */
  primaryConstructor: Option[Constructor],

  /** All constructors of this class, including the primary constructor. */
  constructors: List[Constructor]

  /** The companion of this template, or none. If a class and an object are defined as a pair of the same name, the
    * other entity of the pair is the companion. */
  // companion: Option[DocTemplate],

  /** The implicit conversions this template (class or trait, objects and packages are not affected) */
  // conversions: List[ImplicitConversion],

  /** The shadowing information for the implicitly added members */
  // def implicitsShadowing: Map[MemberEntity, ImplicitMemberShadowing]

  /** Classes that can be implcitly converted to this class */
  // def incomingImplicitlyConvertedClasses: List[(DocTemplateEntity, ImplicitConversion)]

  /** Classes to which this class can be implicitly converted to
      NOTE: Some classes might not be included in the scaladoc run so they will be NoDocTemplateEntities */
  // outgoingImplicitlyConvertedClasses: List[(Template, TypeEntity, ImplicitConversion)]

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
) 
