package com.digitalfrontiers.dataconnectiontool.services

import com.digitalfrontiers.dataconnectiontool.components.ICustomFunction
import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.with
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