package com.synebula.gaea.domain.repository

import com.synebula.gaea.domain.model.IAggregateRoot

interface IRepositoryTyped {
    /**
     * 插入单个对象。
     *
     * @param obj 需要插入的对象。
     * @return 返回原对象，如果对象ID为自增，则补充自增ID。
     */
    fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> add(obj: TAggregateRoot, clazz: Class<TAggregateRoot>)

    /**
     * 更新对象。
     *
     * @param obj 需要更新的对象。
     * @return
     */
    fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> update(obj: TAggregateRoot, clazz: Class<TAggregateRoot>)

    /**
     * 通过id删除该条数据
     *
     * @param id    id
     * @param clazz 操作数据的类型
     */
    fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> remove(id: TKey, clazz: Class<TAggregateRoot>)

    /**
     * 根据ID获取对象。
     *
     * @param id    id
     * @param clazz 操作数据的类型
     * @return 聚合根
     */
    fun <TAggregateRoot : IAggregateRoot<TKey>, TKey> get(id: TKey, clazz: Class<TAggregateRoot>): TAggregateRoot
}
