package com.digitalfrontiers.components

data class Format(
    val requiredFields: List<String>,
    val optionalFields: List<String>,
) {

    init {
        val intersection = requiredFields intersect optionalFields.toSet()
        require(intersection.isEmpty()) {
            "Fields cannot be both required and optional. Overlapping fields: $intersection"
        }
    }

    /**
     * @return A list containing the union of all required and all optional fields
     */
    fun getAllFields(): List<String> {
        return requiredFields + optionalFields
    }

}
