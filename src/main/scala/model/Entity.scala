package ornicar.scalex
package model

/**
 * An entity in a Scaladoc universe. Entities are declarations in the program and correspond to symbols in the
 * compiler. Entities model the following Scala concepts:
 *  - classes and traits;
 *  - objects and package;
 *  - constructors;
 *  - methods;
 *  - values, lazy values, and variables;
 *  - abstract type members and type aliases;
 *  - type and value parameters;
 *  - annotations.
 */
case class Entity(
    /**
     * The name of the entity. Note that the name does not qualify this entity uniquely; use its `qualifiedName`
     * instead.
     */
    name: String,

    /**
     * The qualified name of the entity. This is this entity's name preceded by the qualified name of the template
     * of which this entity is a member. The qualified name is unique to this entity.
     */
    qualifiedName: String,

    /** The template of which this entity is a member. */
    // def inTemplate: TemplateEntity

    /**
     * The list of entities such that each is a member of the entity that follows it; the first entity is always this
     * entity, the last the root package entity.
     */
    // def toRoot: List[Entity]

    /** The annotations attached to this entity, if any. */
    // def annotations: List[Annotation]

    /** The kind of the entity */
    kind: String /** Whether or not the template was defined in a package object */ // def inPackageObject: Boolean
    /** Indicates whether this entity lives in the types namespace (classes, traits, abstract/alias types) */ // def isType: Boolean
    ) {

  override def toString = qualifiedName
}
