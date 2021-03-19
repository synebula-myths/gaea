package com.synebula.gaea.app.component.aop

import java.lang.Exception

interface AnnotationHandler {
    fun handle(clazz: Class<Any>, func: String, args: Array<Any>, exception: Exception?)
}