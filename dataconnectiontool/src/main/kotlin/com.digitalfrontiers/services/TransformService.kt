package com.digitalfrontiers.services

import com.digitalfrontiers.Transform
import com.digitalfrontiers.components.CustomFunction
import com.digitalfrontiers.transform.ListOf
import com.digitalfrontiers.transform.Transformation
import com.digitalfrontiers.with
import org.springframework.stereotype.Service

@Service
class TransformService(
    private val customFunctions: List<CustomFunction> = emptyList(),
) {

    /**
     * Creates a [Transform] object that applies [spec] to each element of the input list
     * and registers [customFunctions] on it.
     */
    fun createTransform(spec: Transformation.Record): Transform =
        Transform to {
            ListOf {
                spec
            }
        } with {
            for (cf in customFunctions) {
                function(cf.id, cf::implementation)
            }
        }
}