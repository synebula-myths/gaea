package com.synebula.gaea.spring.aop.annotation

import com.synebula.gaea.spring.aop.handler.AccessLogHandler

@Target(AnnotationTarget.FUNCTION)
@Handler(AccessLogHandler::class)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccessLog