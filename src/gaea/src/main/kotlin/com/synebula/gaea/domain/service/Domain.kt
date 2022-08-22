package com.synebula.gaea.domain.service

import com.synebula.gaea.domain.model.IAggregateRoot
import kotlin.reflect.KClass

/**
 * 声明服务依赖的聚合根，若服务没有实现类则可以根据依赖项自动组装服务。
 *
 * @param clazz 依赖的聚合根类型
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Domain(val clazz: KClass<out IAggregateRoot<*>>)
