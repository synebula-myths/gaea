package com.synebula.gaea.spring.aop.handler

import java.lang.reflect.Method

/**
 * 注解对应的方法处理对象接口
 */
interface AnnotationHandler {

    /**
     * 处理“被注解方法”的方法
     * @param obj 处理的类对象
     * @param func 处理的方法
     * @param args 处理的方法参数信息
     * @param exception 处理的异常信息
     */
    fun handle(obj: Any, func: Method, args: Array<Any>, exception: Exception? = null)
}