package com.synebula.gaea.spring.aop.handler

import java.lang.reflect.Method

interface AnnotationHandler {
    fun handle(clazz: Class<Any>, func: Method, args: Array<Any>, exception: Exception? = null)
}