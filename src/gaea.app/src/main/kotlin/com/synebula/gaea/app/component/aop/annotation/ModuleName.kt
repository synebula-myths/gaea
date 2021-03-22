package com.synebula.gaea.app.component.aop.annotation

/**
 * 模块的业务名称
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleName(val value: String)
