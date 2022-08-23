package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.DataMessage
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.log.ILogger


/**
 * 继承该接口，表明对象为领域服务。
 * @author alex
 * @version 0.0.1
 * @since 2016年9月18日 下午2:23:15
 */
interface ISimpleService<TAggregateRoot : IAggregateRoot<ID>, ID> {
    /**
     * 日志组件。
     */
    var logger: ILogger

    /**
     * 增加对象
     *
     * @param root 增加对象命令
     */
    fun add(root: TAggregateRoot): DataMessage<ID>

    /**
     * 增加对象
     *
     * @param roots 增加对象命令列表
     */
    fun add(roots: List<TAggregateRoot>)

    /**
     * 更新对象
     *
     * @param id 对象ID
     * @param root 更新对象命令
     */
    fun update(id: ID, root: TAggregateRoot)

    /**
     * 批量更新对象
     *
     * @param roots 更新对象命令列表
     */
    fun update(roots: List<TAggregateRoot>)

    /**
     * 增加对象
     * @param id 对象ID
     */
    fun remove(id: ID)
}
