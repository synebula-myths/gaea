package com.synebula.gaea.domain.service

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import kotlin.reflect.KClass

/**
 * 声明服务的依赖项，若服务没有实现类则可以根据依赖项自动组装服务。
 *
 * @param clazz 依赖的聚合根类型
 * @param repo 依赖的[IRepository]类型
 */
annotation class ServiceDependency(
    val clazz: KClass<out IAggregateRoot<*>>,
    val repo: KClass<out IRepository<*, *>>,
)
