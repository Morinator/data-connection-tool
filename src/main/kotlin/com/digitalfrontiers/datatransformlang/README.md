# Data Transformation Language

Written by [Digital Frontiers](https://www.digitalfrontiers.de/).

## Introduction

_data transformation language_ (dtl) is a tool to transform data given by JSON, CSV, YAML (and potentially more).
It provides a concise, declarative and flexible way to define data transformations.

A primary use case is the integration of heterogeneous APIs. Suppose that given JSON-data taken from some API,
a part of it needs to be selected and converted to CSV, with some of the columns being renamed to fit the schema of 
a different API.
This can be directly modeled using the respective parses and serializers of _dtl_ together 
with a user-defined transformation-object.


## Usage

Here is a brief example, which shows input parsing, construction of a transformation, applying this transformation,
and serializing the output:

```kotlin
val input = """
    {
      "michael" : {
        "grade" : 2,
        "major" : "biology"
      },
      "hillary" : {
        "grade" : 5
      },
      "josh" : { }
    }
""".trimIndent()

val data = JSON.parse(input)
val transformation = Object {
    "gradeOfMichael" to Input("$.michael.grade")
}

val result = applyTransform(data, transformation)
val resultString= YAMLSerializer().serialize(result)
/*
 * ---
 * gradeOfMichael: 2
 */

 ```

### Installation

Add the following Gradle dependency:

TODO Gradle dependency

### User Guide

TODO link to online view of Docs, similar to JavaDoc???

TODO License ???