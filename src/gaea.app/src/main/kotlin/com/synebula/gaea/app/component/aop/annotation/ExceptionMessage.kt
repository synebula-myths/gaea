package com.synebula.gaea.app.component.aop.annotation

import java.lang.annotation.Inherited

/**
 * 标记方法安全执行，由AOP负责try catch异常
 *
 * @param message 异常消息
 */
@Inherited
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExceptionMessage(val message: String)
