Here's the plan:

[source (files)]
-- Indexer ->
[database (file)]

[source (files)]
-- SourceToNscDoc ->
[nsc doc (scala tree)]
-- NscDocToModel ->
[model (scala tree)]
-- ModelToBinary ->
<- BinaryToModel --
[binary (bytes array)]
-- BinaryToFile ->
<- FileToBinary --
[database (file)]
-- FileToHeader ->
[header (string)]

[model (scala tree)]
-- ModelToDocument ->
[document (scala list)]
-- DocumentToElastic ->
<- ElasticToDocument --
[elastic (json)]

[elastic response (java object)]
-- ElasticToResult ->
[result (scala list)]
-- ResultToJson ->
[result (json)]
