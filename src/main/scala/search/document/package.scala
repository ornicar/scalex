package ornicar.scalex
package search

package object document {

  type Docs = List[Doc]
  type ScopedDocs = Map[ProjectId, List[Doc]]
}
