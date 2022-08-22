package com.synebula.gaea.app.component.aop.annotation

import com.synebula.gaea.app.component.aop.handler.AnnotationHandler
import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Handler(val value: KClass<out AnnotationHandler>)