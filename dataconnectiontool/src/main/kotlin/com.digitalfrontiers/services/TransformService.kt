package com.digitalfrontiers.services

import com.digitalfrontiers.components.ICustomFunction
import com.digitalfrontiers.Transform
import com.digitalfrontiers.transform.Specification
import com.digitalfrontiers.with
import org.springframework.stereotype.Service

@Service
class TransformService(
    private val customFunctions: List<ICustomFunction>
) {
    fun createTransform(spec: Specification): Transform {
        return Transform to {
            spec
        } with {
            for (cf in customFunctions) {
                function(cf.id, cf::implementation)
            }
        }
    }
}