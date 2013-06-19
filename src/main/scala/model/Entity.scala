package org.scalex
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
    qualifiedName: String) {

  def splitQualifiedName = qualifiedName.split('.').toList

  def shortQualifiedName = (splitQualifiedName.reverse match {
    case Nil          ⇒ Nil
    case head :: more ⇒ head :: more.map(_ take 1)
  }).reverse mkString "."

  override def toString = qualifiedName
}
