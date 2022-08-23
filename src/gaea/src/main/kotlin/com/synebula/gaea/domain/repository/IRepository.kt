package com.synebula.gaea.domain.repository

import com.synebula.gaea.domain.model.IAggregateRoot

/**
 * 定义了提供增删改的仓储接口。
 * 本接口泛型放置到方法上，并需要显式提供聚合根的class对象
 */
interface IRepository<TAggregateRoot : IAggregateRoot<ID>, ID> {

    /**
     * 仓储的对象类
     */
    var clazz: Class<TAggregateRoot>

    /**
     * 插入单个对象。
     *
     * @param obj 需要插入的对象。
     * @return 返回原对象，如果对象ID为自增，则补充自增ID。
     */
    fun add(obj: TAggregateRoot)

    /**
     * 插入多个个对象。
     *
     * @param list 需要插入的对象。
     * @return 返回原对象，如果对象ID为自增，则补充自增ID。
     */
    fun add(list: List<TAggregateRoot>)


    /**
     * 更新对象。
     *
     * @param obj 需要更新的对象。
     * @return
     */
    fun update(obj: TAggregateRoot)

    /**
     * 更新多个个对象。
     *
     * @param list 需要新的对象。
     */
    fun update(list: List<TAggregateRoot>)

    /**
     * 通过id删除该条数据
     *
     * @param id 对象ID。
     * @return
     */
    fun remove(id: ID)

    /**
     * 根据ID获取对象。
     *
     * @param id 对象ID。
     * @return
     */
    fun get(id: ID): TAggregateRoot?


    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return int
     */
    fun count(params: Map<String, Any>?): Int

}

