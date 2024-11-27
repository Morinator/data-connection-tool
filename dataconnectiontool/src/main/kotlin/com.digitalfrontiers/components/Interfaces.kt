package com.digitalfrontiers.components

import com.digitalfrontiers.Format
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

interface IEndpoint {
    val id: String

    val format: Format
}

interface ISource: IEndpoint {
    fun hasData(): Boolean
    fun fetch(): Map<String, String>
}

interface ISink: IEndpoint {
    fun put(data: Map<String, String>)
}

interface ICustomFunction {
    val id: String

    fun implementation(args: List<Any?>): Any?
}

@Target(AnnotationTarget.CLASS) // Nur für Klassen
@Retention(AnnotationRetention.RUNTIME) // Zur Laufzeit verfügbar
@Component
@Scope("prototype")
annotation class Endpoint