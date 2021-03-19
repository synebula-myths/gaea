package com.synebula.gaea.app.component.aop.annotation

/**
 * 标记方法安全执行，由AOP负责try catch异常
 *
 * @param errorMessage 异常消息
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SafeExec(val errorMessage: String)
