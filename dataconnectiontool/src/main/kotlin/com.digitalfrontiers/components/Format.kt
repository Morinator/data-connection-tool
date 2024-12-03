package com.digitalfrontiers.components

data class Format(
    val mandatoryFields: List<String>,
    val optionalFields: List<String>,
) {

    init {
        val intersection = mandatoryFields intersect optionalFields.toSet()
        require(intersection.isEmpty()) {
            "Fields cannot be both mandatory and optional. Overlapping fields: $intersection"
        }
    }

    fun getAllFields(): List<String> {
        return mandatoryFields + optionalFields
    }

}
