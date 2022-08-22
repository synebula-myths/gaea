package com.synebula.gaea.domain.repository

import com.synebula.gaea.domain.model.IAggregateRoot

/**
 * Repository 工厂接口。 定义了Repository的创建方法。
 */
interface IRepositoryFactory {

    /**
     * 创建原始类型的IRepository接口类型
     */
    fun createRawRepository(clazz: Class<*>): IRepository<*, *>

    /**
     * 创建指定类型的IRepository接口类型
     */
    fun <T : IAggregateRoot<I>, I> createRepository(clazz: Class<T>): IRepository<T, I>
}