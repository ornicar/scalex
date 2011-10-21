# Hoogle-like documentation search engine for scala library.

    "map" Search for the text "map"
    "list map" Search for the text "list" and the text "map"
    "A => A" Search for the type "A => A"
    ": A => A" Search for the type "A => A"
    "a" Search for the text "a"
    "map : List[A] => (A => B) => List[B]" Search for the text "map" and the type "List[A] => (A => B) => List[B]" 

Searches can be either textual (a list of words), or by type (a type signature) or both. A type search may optionally start with a `:` symbol. A search is considered a text search unless it contains a combination of text and symbols, or if it starts with `:`. To search for both a type and a name, place a `:` between them, for example "size : List[A] => Int"

Scalex uses mongodb to store informations about scala library functions.

## Usage

Run it with sbt.

### Create the database

    > run dump /path/to/scala/library

### Query the database

    > run search List[A] => (A => B) => List[B]

    * mapConserve
      collection.immutable.List#mapConserve[B<Some(A)>Some(A)]: List[+A] ⇒ (f: (A => B)) ⇒ List[B]
      Builds a new list by applying a function to all elements of this list.Like xs map f, but returns xs unchanged if functionf maps all elements to themselves (wrt eq).

    * reverseMap
      collection.immutable.List#reverseMap[B]: List[+A] ⇒ (f: (A => B)) ⇒ List[B]
      Builds a new collection by applying a function to all elements of this list andcollecting the results in reversed order.

    * map
      collection.immutable.List#map[B]: List[+A] ⇒ (f: (A => B)) ⇒ List[B]
      Builds a new collection by applying a function to all elements of this list.

## HTTP

A HTTP service is being developed.
