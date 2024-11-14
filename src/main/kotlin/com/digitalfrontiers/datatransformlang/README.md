# Data Transformation Language

The _data transformation language_ (dtl) is a tool to transform data given by JSON, CSV, YAML (and potentially more).
It provides a concise, declarative and flexible way to define data transformations.

A primary use case is the integration of heterogeneous APIs. Suppose that given JSON-data taken from some API,
a part of it needs to be selected and converted to CSV, with some of the columns being renamed to fit the schema of 
a different API.
This can be directly modeled using the respective parses and serializers of _dtl_ together 
with a user-defined transformation-object.

Here is a brief example, which shows the usage of a transformation on data defined via Kotlin objects:
```kotlin
val grades: Data = mapOf(
    "michael" to mapOf(
        "grade" to 2,
        "major" to "biology"
    ),
    "hillary" to mapOf(
        "grade" to 5
    ),
    "josh" to emptyMap()
)
val transformation = Object {
    "gradeOfJosh" to Input("$.michael.grade")
}
val result = applyTransform(grades, transformation)
println(result) // {gradeOfJosh=2}
 ```
