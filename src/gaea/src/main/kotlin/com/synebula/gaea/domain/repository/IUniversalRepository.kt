package com.synebula.gaea.domain.repository

import com.synebula.gaea.domain.model.IAggregateRoot

/**
 * 定义了提供增删改的仓储接口。
 * 本接口泛型放置到方法上，并需要显式提供聚合根的class对象
 */
interface IUniversalRepository {
    /**
     * 插入单个对象。
     *
     * @param root 需要插入的对象。
     * @return 返回原对象，如果对象ID为自增，则补充自增ID。
     */
    fun <TAggregateRoot : IAggregateRoot<ID>, ID> add(root: TAggregateRoot, clazz: Class<TAggregateRoot>)

    /**
     * 插入多个个对象。
     *
     * @param roots 需要插入的对象。
     */
    fun <TAggregateRoot : IAggregateRoot<ID>, ID> add(roots: List<TAggregateRoot>, clazz: Class<TAggregateRoot>)

    /**
     * 更新对象。
     *
     * @param root 需要更新的对象。
     * @return
     */
    fun <TAggregateRoot : IAggregateRoot<ID>, ID> update(root: TAggregateRoot, clazz: Class<TAggregateRoot>)

    /**
     * 更新多个个对象。
     *
     * @param roots 需要更新的对象。
     */
    fun <TAggregateRoot : IAggregateRoot<ID>, ID> update(roots: List<TAggregateRoot>, clazz: Class<TAggregateRoot>)

    /**
     * 通过id删除该条数据
     *
     * @param id    id
     * @param clazz 操作数据的类型
     */
    fun <TAggregateRoot : IAggregateRoot<ID>, ID> remove(id: ID, clazz: Class<TAggregateRoot>)

    /**
     * 根据ID获取对象。
     *
     * @param id    id
     * @param clazz 操作数据的类型
     * @return 聚合根
     */
    fun <TAggregateRoot : IAggregateRoot<ID>, ID> get(id: ID, clazz: Class<TAggregateRoot>): TAggregateRoot?


    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return int
     */
    fun <TAggregateRoot> count(params: Map<String, String>?, clazz: Class<TAggregateRoot>): Int
}
