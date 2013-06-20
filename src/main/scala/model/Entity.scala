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
     * The qualified name of the entity. This is this entity's name preceded by the qualified name of the template
     * of which this entity is a member. The qualified name is unique to this entity.
     */
    qualifiedName: String) {

  def name = ~namePile.headOption

  // names composing the qualified name, in reverse order
  lazy val namePile = qualifiedName.split(Array('.', ' ', '#')).toList.reverse map (_.trim) filterNot (_.isEmpty)

  def shortQualifiedName = (namePile match {
    case Nil          ⇒ Nil
    case head :: more ⇒ head :: more.map(_ take 1)
  }).reverse mkString "."

  override def toString = qualifiedName
}
