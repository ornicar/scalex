package org.scalex

sealed trait ScalexException

final class InvalidHeaderException(str: String)
  extends Exception("Invalid project header: " + str)
  with ScalexException

final class InvalidProjectVersionException(version: String)
  extends Exception("Invalid project version: " + version)
  with ScalexException

final class OutdatedDatabaseException(name: String)
  extends Exception(s"The database $name is too old and must be rebuilded")
  with ScalexException

final class SearchFailureException
  extends Exception("Search failure")
  with ScalexException

final class BadArgumentException(msg: String)
  extends IllegalArgumentException(msg)
  with ScalexException

final class InvalidQueryException(msg: String)
  extends IllegalArgumentException(msg)
  with ScalexException
  
final class InvalidDatabaseException(msg: String) 
  extends Exception(s"[Invalid database] $msg") 
  with ScalexException 
