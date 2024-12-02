package com.digitalfrontiers.components

data class Format(
    val mandatoryFields: List<String>,
    val optionalFields: List<String>,
) {

    fun getAllFields(): List<String> {
        return mandatoryFields + optionalFields
    }

}
