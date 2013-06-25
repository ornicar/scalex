package org.scalex
package model

import org.specs2.mutable._

class BlockSpec extends Specification {

  "Construct block" should {
    "similar text and html" in {
      Block("hello", "") must_== Block("hello", None)
      Block("hello", "<a>oh yeah</a>") must_== Block("hello", "<a>oh yeah</a>".some)
      Block("hello", "hello") must_== Block("hello", None)
      Block("hello", "<p>hello</p>") must_== Block("hello", None)
      Block("hello", """ <p>hello</p>""") must_== Block("hello", None)
      Block("hello", """  
 <p> 
   
   hello
</p> 
   """) must_== Block("hello", None)
    }
  }
}
