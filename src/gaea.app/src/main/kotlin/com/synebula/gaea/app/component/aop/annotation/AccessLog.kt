package com.synebula.gaea.app.component.aop.annotation

import com.synebula.gaea.app.component.aop.handler.AccessLogHandler

@Target(AnnotationTarget.FUNCTION)
@Handler(AccessLogHandler::class)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccessLog