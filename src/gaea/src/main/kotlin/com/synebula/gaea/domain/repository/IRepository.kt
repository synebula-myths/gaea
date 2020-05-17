package com.synebula.gaea.domain.repository

import com.synebula.gaea.domain.model.IAggregateRoot

/**
 * 继承本接口表示对象为仓储类。
 *
 * @param <TAggregateRoot> this T is the parameter
 * @author alex
 */
interface IRepository<TAggregateRoot : IAggregateRoot<TKey>, TKey> {

    /**
     * 仓储的对象类
     */
    var clazz: Class<TAggregateRoot>?

    /**
     * 插入单个对象。
     *
     * @param obj 需要插入的对象。
     * @return 返回原对象，如果对象ID为自增，则补充自增ID。
     */
    fun add(obj: TAggregateRoot)

    /**
     * 更新对象。
     *
     * @param obj 需要更新的对象。
     * @return
     */
    fun update(obj: TAggregateRoot)

    /**
     * 通过id删除该条数据
     *
     * @param id
     * @return
     */
    fun remove(id: TKey)

    /**
     * 根据ID获取对象。
     *
     * @param id 对象ID。
     * @return
     */
    fun get(id: TKey): TAggregateRoot

    /**
     * 根据ID获取对象。
     *
     * @param id    id
     * @param clazz 操作数据的类型
     * @return 聚合根
     */
    fun <T : IAggregateRoot<TKey>, TKey> get(id: TKey, clazz: Class<T>): T

}
