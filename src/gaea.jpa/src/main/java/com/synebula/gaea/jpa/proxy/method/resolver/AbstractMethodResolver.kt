package com.synebula.gaea.jpa.proxy.method.resolver

import java.lang.reflect.Method

/**
 * 解决JPA方法参数的映射
 *
 * @param targetMethodName 目标方法名称
 */
abstract class AbstractMethodResolver(var targetMethodName: String) {


    /**
     * 方法相关实体类型
     */
    lateinit var entityClazz: Class<*>

    /**
     * 目标方法形参类型列表
     *
     */
    lateinit var targetMethodParameters: Array<out Class<*>>

    /**
     * 源方法形参类型列表
     *
     */
    lateinit var sourceMethodParameters: Array<out Class<*>>

    constructor(targetMethodName: String, entityClazz: Class<*>) : this(targetMethodName) {
        this.entityClazz = entityClazz
    }

    /**
     * 解析映射实参
     *
     * @param args 实参列表
     * @return 映射后的实参列表
     */
    abstract fun mappingArguments(args: Array<Any>): Array<Any>

    /**
     * 解析映射方法结果
     *
     * @param result 方法结果
     * @return 映射后的方法结果
     */
    abstract fun mappingResult(result: Any): Any

    /**
     * 获取源方法形参类型列表
     */
    open fun sourceMethodParameters(vararg params: Class<*>): AbstractMethodResolver {
        this.sourceMethodParameters = params
        return this
    }

    /**
     * 设置目标方法形参类型列表
     */
    open fun targetMethodParameters(vararg params: Class<*>): AbstractMethodResolver {
        this.targetMethodParameters = params
        return this
    }

    /**
     * 匹配方法名(目标方法)/参数是否复合
     *
     * @param method     需要匹配的方法
     * @param methodType 需要匹配的方法类型
     * @return ture/false
     */
    fun match(method: Method, methodType: MethodType): Boolean {
        var methodParameters = sourceMethodParameters
        var matched = true

        // 匹配目标方法的时候额外匹配下方法名
        if (methodType == MethodType.TargetMethod) {
            methodParameters = targetMethodParameters
            matched = method.name == targetMethodName
        }

        // 如果[目标]方法名匹配, 判断参数是否匹配
        matched = matched && method.parameterCount == methodParameters.size
        if (matched) {
            for (i in methodParameters.indices) {
                val parameterTypes = method.parameterTypes
                if (methodParameters[i] != parameterTypes[i]) {
                    matched = false
                    break
                }
            }
        }
        return matched
    }

    enum class MethodType {
        SourceMethod, TargetMethod
    }
}