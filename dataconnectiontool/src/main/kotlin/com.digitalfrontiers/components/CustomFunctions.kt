package com.digitalfrontiers.dataconnectiontool.components

import org.springframework.stereotype.Component

// ========================

@Component
class Interpolate: ICustomFunction {

    override val id = "interpolate"

    override fun implementation (args: List<Any?>): Any
        {
            return args.drop(1).fold(args[0] as String) { acc, arg -> acc.replaceFirst("{}", arg.toString()) }
        }
}

@Component
class BranchOnEquals: ICustomFunction {
    override val id: String = "branchOnEquals"

    override fun implementation(args: List<Any?>): Any? {
        return if (args[0] == args[1])
            args[2]
        else
            args[3]
    }
}