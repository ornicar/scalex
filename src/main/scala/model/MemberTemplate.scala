package ornicar.scalex
package model

/**
 * An inherited template that was not documented in its original owner - example:
 *  in classpath:  trait T { class C } -- T (and implicitly C) are not documented
 *  in the source: trait U extends T -- C appears in U as a MemberTemplateImpl
 *    -- that is, U has a member for it but C doesn't get its own page
 */
case class MemberTemplate(

    /** a MemberTemplate is a Member */
    member: Member,

    /** a MemberTemplate is a Template */
    template: Template,

    /** a MemberTemplate is a HigherKinded */
    higherKinded: HigherKinded,

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
    parentTypes: List[TemplateAndType]) {

  override def toString = template.toString
}
