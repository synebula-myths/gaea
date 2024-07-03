package com.synebula.gaea.db.query

/**
 * Query 工厂接口。 定义了Query的创建方法。
 */
interface IQueryFactory {

    /**
     * 创建原始类型的IQuery接口类型
     */
    fun createRawQuery(clazz: Class<*>): IQuery<*, *>

    /**
     * 创建指定类型的IQuery接口类型
     */
    fun <T, I> createQuery(clazz: Class<T>): IQuery<T, I>
}