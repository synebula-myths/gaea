package com.synebula.gaea.domain.repository.context

import com.synebula.gaea.domain.model.IAggregateRoot

/**
 * 继承自IUnitOfWork，表示实现了工作单元模式的上下文接口。
 *
 * @author alex
 */
interface IContext : IUnitOfWork {
    /**
     * 将指定的聚合根标注为“新建”状态。
     * @param obj 聚合根
     */
    fun <T : IAggregateRoot<ID>, ID> add(obj: T)

    /**
     * 将指定的聚合根标注为“更改”状态。
     *
     * @param obj 聚合根
     */
    fun <T : IAggregateRoot<ID>, ID> update(obj: T)

    /**
     * 将指定的聚合根标注为“删除”状态。
     *
     * @param obj 聚合根
     */
    fun <T : IAggregateRoot<ID>, ID> remove(obj: T)
}
