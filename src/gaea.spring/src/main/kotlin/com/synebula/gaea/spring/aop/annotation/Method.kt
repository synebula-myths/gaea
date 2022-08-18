package com.synebula.gaea.spring.aop.annotation

import java.lang.annotation.Inherited

/**
 * 标记方法名称，由AOP负责记录异常时使用该名称
 *
 * @param name 方法名称
 */
@Inherited
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Method(val name: String)
