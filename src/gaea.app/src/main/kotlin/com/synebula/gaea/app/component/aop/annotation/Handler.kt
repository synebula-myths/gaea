package com.synebula.gaea.app.component.aop.annotation

import com.synebula.gaea.app.component.aop.handler.AnnotationHandler
import kotlin.reflect.KClass

/**
 * 标注在注解，表明注解的处理方法
 *
 * @param value 注解处理方法
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Handler(val value: KClass<out AnnotationHandler>)