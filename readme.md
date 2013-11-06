# Hoogle-like documentation search engine for scala library.

- [See it in action](http://scalex.org)

- Read the [HTTP API documentation](https://github.com/ornicar/scalex/blob/master/http-api-documentation.md)

## Usage

Scalex is a library intended to be a dependency of an application server like [scalex-http](https://github.com/ornicar/scalex-http)

But you can run scalex directly to make it index libraries.

### CLI

Commands have the following format:

    <config_file> <command_name> <command_args>

### Create the database

From sbt console:

    > run /path/to/scalex.conf dump arbitrary.package_name /path/to/arbitrary/src

For example, when I index scalaz I run:

    > run ./scalex.conf dump scalaz /home/thib/scalaz/core/src

### Create the binary index

    > run ./scalex.conf index

### Query the database

    > run ./scalex.conf search list map

    * map
      collection.immutable.List#map[B]: List[+A] ⇒ (f: (A => B)) ⇒ List[B]
      Builds a new collection by applying a function to all elements of this list.

    * mapConserve
      collection.immutable.List#mapConserve[B<Some(A)>Some(A)]: List[+A] ⇒ (f: (A => B)) ⇒ List[B]
      Builds a new list by applying a function to all elements of this list.Like xs map f, but returns xs unchanged if functionf maps all elements to themselves (wrt eq).

    * reverseMap
      collection.immutable.List#reverseMap[B]: List[+A] ⇒ (f: (A => B)) ⇒ List[B]
      Builds a new collection by applying a function to all elements of this list andcollecting the results in reversed order.

## HTTP

A HTTP API is available, [see the doc](https://github.com/ornicar/scalex/blob/master/doc/http-api.md)
