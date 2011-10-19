# SCALEX HTTP API

Scalex offers an HTTP API allowing to search for scala functions based on keywords and signatures.

Use this API to build new clients!

## Clients using this API

- [scalex.org](http://scalex.org) (JavaScript)
- [scalex CLI](https://github.com/jonifreeman/Scalex-CLI) (scala)

Wanted: Vim, Emacs, Eclipse, and more!

## Try it

    curl http://api.scalex.org/?q=list+map

    curl http://api.scalex.org/?q=list+map&page=2

    curl http://scalex-web/?q=map:%20List[A]%20=%3E%20(A%20=%3E%20B)%20=%3E%20List[B]

## JSON output

The results are paginated by 15. Here is an output example for `http://api.scalex.org/?q=list+map&page=2`

    {
      query: "list map"               // Query we are searching for
      nbResults: 26                   // Total number of results
      page: 2                         // Page requested, defaults to 1
      nbPages: 3                      // Number of pages of results
      results: [                      // List of functions found on this page
        {
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
