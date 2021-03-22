package com.synebula.gaea.app.component.aop.handler

import java.lang.Exception
import java.lang.reflect.Method

interface AnnotationHandler {
    fun handle(clazz: Class<Any>, func: Method, args: Array<Any>, exception: Exception? = null)
}