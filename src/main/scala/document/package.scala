package org.scalex

package object document {

  type Docs = List[Doc]
  type ScopedDocs = Map[ProjectId, List[Doc]]
}
