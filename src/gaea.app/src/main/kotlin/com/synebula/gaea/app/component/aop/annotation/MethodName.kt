package com.synebula.gaea.app.component.aop.annotation

import java.lang.annotation.Inherited

/**
 * 标记方法名称，由AOP负责记录异常时使用该名称
 *
 * @param name 异常消息
 */
@Inherited
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodName(val name: String)
