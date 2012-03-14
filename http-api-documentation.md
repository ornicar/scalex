# SCALEX HTTP API

Scalex offers an HTTP API allowing to search for scala functions based on keywords and signatures.

## Clients using this API

- [scalex.org](http://scalex.org) (JavaScript)
- [scalex CLI](https://github.com/jonifreeman/Scalex-CLI) (scala)
- [#scala multibot](https://github.com/lopex/multibot/blob/master/src/main/scala/org/multibot/Multibot.scala)

## Try it

    curl http://api.scalex.org/?q=list+map

    curl http://api.scalex.org/?q=list+map&per_page=50&page=2

    curl http://scalex-web/?q=map:%20List[A]%20=%3E%20(A%20=%3E%20B)%20=%3E%20List[B]

## Query parameters

    q: String               the query to search
    page: Int = 1           the pagination page
    per_page: Int = 20      number of results per page
    callback: String = ""   optional jsonp callback name

## JSON output

Here is an output example for `http://api.scalex.org/?q=list+map&page=2`

    {
      query: "list map"               // Query we are searching for
      nbResults: 26                   // Total number of results
      page: 2                         // Page requested, defaults to 1
      nbPages: 3                      // Number of pages of results
      milliseconds: 12                // Duration of the query
      results: [                      // List of functions found on this page
        {
          docUrl: "http://www.scala-lang.org/api/current/scala/collection/immutable/List#map%5BB%5D%28%28A%29%20%E2%87%92%20B%29%3AList%5BB%5D"
          name: "map"                 // Function short name
          qualifiedName: "scala.collection.immutable.ListMap#map" // Function full name
          typeParams: "[C]"           // Function type params
          resultType: "Map[A, C]"     // Type of the returned value
          valueParams: "f: (B => C)"  // Params the function accepts
          signature: "ListMap[A, +B] => (f: (B => C)) => Map[A, C]" // Normalized signature of the function
          package: "scala"            // Name of the package containing the function
          deprecation: {              // Deprecation message if the function is deprecated, otherwise nothing
            html:                     // HTML deprecation message
            txt:                      // TXT deprecation message
          }
          parent: {                   // class|trait|object containing the function
            name: "ListMap"           // Parent short name
            qualifiedName: "scala.collection.immutable.ListMap" // Parent full name
            typeParams: "[A, +B]"     // Parent type params
          }
          comment: {                  // Function comment, structured
            short: 
            body: 
            authors: 
            see: 
            result: 
            throws: 
            typeParams: 
            valueParams: 
            version: 
            since: 
            todo: 
            note: 
            example: 
            constructor: 
            source: 
          }
          ... more results
        ]
      }
    }
