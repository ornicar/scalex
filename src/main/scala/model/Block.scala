package org.scalex
package model

case class Block(
  txt: String, 
  // let empty if similar to txt
  html: Option[String]) 
