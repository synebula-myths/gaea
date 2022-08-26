package com.synebula.gaea.jpa.proxy.method.resolver

import java.util.*

/**
 * 默认返回全部
 */
class DefaultMethodResolver(targetMethodName: String) : AbstractMethodResolver(targetMethodName) {

    override fun mappingArguments(args: Array<Any>): Array<Any> {
        return args
    }

    override fun mappingResult(result: Any): Any {
        if (result is Optional<*>) {
            return result.orElse(null)
        }
        return result
    }
}