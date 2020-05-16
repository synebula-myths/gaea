package com.synebula.gaea.domain.repository

import com.synebula.gaea.domain.model.complex.IComplexAggregateRoot

/**
 * 继承本接口表示对象为仓储类。
 *
 * @param <TAggregateRoot> this T is the parameter
 * @author alex
 */
interface IComplexRepository<TAggregateRoot : IComplexAggregateRoot<TKey, TSecond>, TKey, TSecond> {

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
     * @param key 对象ID。
     * @param secondary 对象副主键。
     * @return
     */
    fun remove(key: TKey, secondary: TSecond)

    /**
     * 根据ID获取对象。
     *
     * @param key 对象ID。
     * @return
     */
    operator fun get(key: TKey, secondary: TSecond): TAggregateRoot
}
