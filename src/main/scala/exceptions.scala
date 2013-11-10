package org.scalex

sealed trait ScalexException

final class InvalidProjectNameException(name: String)
  extends Exception("Invalid project name: " + name)
  with ScalexException

final class InvalidProjectVersionException(version: String)
  extends Exception("Invalid project version: " + version)
  with ScalexException

final class OutdatedDatabaseException(name: String)
  extends Exception("The database %s is too old and must be rebuilded" format name)
  with ScalexException

final class BadArgumentException(msg: String)
  extends IllegalArgumentException(msg)
  with ScalexException

final class InvalidQueryException(msg: String)
  extends IllegalArgumentException(msg)
  with ScalexException
  
class InvalidDatabaseException(msg: String) 
  extends RuntimeException 
  with ScalexException {
    override def getMessage = s"[Invalid database] $msg"
  }
