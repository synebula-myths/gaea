package com.synebula.gaea.spring.aop.annotation

/**
 * 模块的业务名称
 * @param name 模块名称
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Module(val name: String)
