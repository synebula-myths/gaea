package com.synebula.gaea.domain.repository

import com.synebula.gaea.domain.model.IAggregateRoot

/**
 * 继承本接口表示对象为仓储类。
 * 定义了提供增删改的仓储接口。
 * 本接口泛型定义在类上，方法中不需要显式提供聚合根的class对象，class对象作为类的成员变量声明。
 *
 * @param TAggregateRoot 聚合根类型
 * @author alex
 */
interface ISpecificRepository<TAggregateRoot : IAggregateRoot<ID>, ID> {

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
    fun get(id: ID): TAggregateRoot


    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return int
     */
    fun <TAggregateRoot> count(params: Map<String, Any>?): Int

}
