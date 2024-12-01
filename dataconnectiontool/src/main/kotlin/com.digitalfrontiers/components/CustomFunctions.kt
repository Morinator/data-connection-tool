package com.digitalfrontiers.components

import org.springframework.stereotype.Component

interface CustomFunction {
    val id: String

    fun implementation(args: List<Any?>): Any?
}

@Component
class Interpolate : CustomFunction {

    override val id = "interpolate"

    /**
     * Implements the string interpolation logic.
     *
     * Replaces each occurrence of `{}` in the initial string (first argument in the list)
     * with the string representation of subsequent arguments, in order.
     *
     * @param args A list of arguments where the first is the string to interpolate,
     *             and the rest are the values to substitute into placeholders.
     * @return The interpolated string after all replacements.
     * @throws ClassCastException if the first argument is not a string.
     * @throws IllegalArgumentException if the list of arguments is empty.
     */
    override fun implementation(args: List<Any?>): Any {
        return args.drop(1).fold(args[0] as String) { acc, arg -> acc.replaceFirst("{}", arg.toString()) }
    }
}


@Component
class BranchOnEquals : CustomFunction {
    override val id: String = "branchOnEquals"

    /**
     * Implements the branching logic based on equality.
     *
     * Compares the first two arguments for equality and returns the third argument if they are equal,
     * or the fourth argument if they are not.
     *
     * @param args A list of four arguments:
     *             1. The first value to compare.
     *             2. The second value to compare.
     *             3. The value to return if the first and second arguments are equal.
     *             4. The value to return if the first and second arguments are not equal.
     * @return The third argument if the first and second arguments are equal, otherwise the fourth argument.
     * @throws IndexOutOfBoundsException if fewer than four arguments are provided.
     */
    override fun implementation(args: List<Any?>): Any? =
        if (args[0] == args[1])
            args[2]
        else
            args[3]
}