package com.synebula.gaea.jpa.proxy.method.resolver

import com.synebula.gaea.jpa.toSpecification

/**
 * 查询方法参数映射
 */
class FindMethodResolver(targetMethodName: String, clazz: Class<*>) : AbstractMethodResolver(targetMethodName, clazz) {

    @Suppress("UNCHECKED_CAST")
    override fun mappingArguments(args: Array<Any>): Array<Any> {
        val params = args[0] as Map<String, String>?
        val specification = params?.toSpecification(entityClazz)
        return if (specification != null) arrayOf(specification) else arrayOf()
    }

    override fun mappingResult(result: Any): Any {
        return result
    }
}