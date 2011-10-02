From Hoogle (http://www.haskell.org/haskellwiki/Hoogle):

    "map" Search for the text "map"
    "con map" Search for the text "map" and the text "con"
    "a -> a" Search for the type "a -> a"
    ":: a -> a" Search for the type "a -> a"
    "a" Search for the text "a"
    ":: a" Search for the type "a"
    "id :: a -> a" Search for the text "id" and the type "a -> a" 

Searches can be either textual (a list of words), or by type (a type signature) or both. A type search may optionally start with a "::" symbol. A search is considered a text search unless it contains a combination of text and symbols, or if it starts with (::). To search for both a type and a name, place a :: between them, for example "undefined :: a"

Searches can be restricted to a particular module with +Module.Name, or to avoid a module with -Module.Name. The command Module.Name.Foo is treated as +Module.Name Foo.

Searches can be restricted to a particular package with +packagename, or to avoid a package with -package. By default Hoogle will search a standard set of packages. 
