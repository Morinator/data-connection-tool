package com.digitalfrontiers

//enum class Type {
//    Null, Boolean, Integer, String, Object, Array
//}

data class Format(val mandatoryFields: List<String>, val optionalFields: List<String>)

interface Source {
    fun getName(): String

    fun getFormat(): Format

    fun process(): Map<String, String>
}

interface Transformer {
    fun getTransform(): Transform
}

interface Sink {
    fun getName(): String

    fun getFormat(): Format

    fun process(data: Map<String, Any?>)
}

class Mapping(
    val source: Source,
    val transformer: Transformer,
    val sink: Sink
) {

    init {
        require(isValidForSource()) { "Specified transformation not valid for source format"}
        require(isValidForSink()) { "Specified transformation not valid for sink format"}
    }

    private fun isValidForSource(): Boolean {
        TODO()
    }

    private fun isValidForSink(): Boolean {
        TODO()
    }

    fun process() {
        val sourceData = this.source.process()
        val transformedData = this.transformer.getTransform().apply(sourceData) as Map<String, Any?>

        return this.sink.process(transformedData)
    }
}