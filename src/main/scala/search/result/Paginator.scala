package org.scalex
package search
package result

import scala.util.{ Try, Success, Failure }

final class Paginator[A] (

    /**
     * Current page results
     */
    val results: Seq[A],

    /**
     * Returns the total number of results.
     */
    val nbResults: Int,

    val currentPage: Int,

    val maxPerPage: Int) {

  /**
   * Returns the previous page.
   */
  def previousPage: Option[Int] = (currentPage != 1) option (currentPage - 1)

  /**
   * Returns the next page.
   */
  def nextPage: Option[Int] = (currentPage != nbPages) option (currentPage + 1)

  /**
   * Returns the number of pages.
   */
  def nbPages: Int = scala.math.ceil(nbResults.toFloat / maxPerPage).toInt

  /**
   * Returns whether we have to paginate or not.
   * This is true if the number of results is higher than the max per page.
   */
  def hasToPaginate: Boolean = nbResults > maxPerPage

  /**
   * Returns whether there is previous page or not.
   */
  def hasPreviousPage: Boolean = previousPage.isDefined

  /**
   * Returns whether there is next page or not.
   */
  def hasNextPage: Boolean = nextPage.isDefined
}
