package com.digitalfrontiers

import com.digitalfrontiers.transform.Transformation
import com.fasterxml.jackson.annotation.JsonProperty

data class Mapping(
    @JsonProperty("source")
    val sourceId: String,

    @JsonProperty("sink")
    val sinkId: String,

    val transformation: Transformation
)