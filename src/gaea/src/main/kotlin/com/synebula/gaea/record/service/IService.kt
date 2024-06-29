package com.synebula.gaea.record.service

import com.synebula.gaea.data.message.DataMessage
import com.synebula.gaea.db.IEntity
import com.synebula.gaea.log.ILogger


/**
 * 继承该接口，表明对象为领域服务。
 * @author alex
 * @version 0.0.1
 * @since 2016年9月18日 下午2:23:15
 */
interface IService<Entity : IEntity<ID>, ID> {
    /**
     * 日志组件。
     */
    var logger: ILogger

    /**
     * 增加对象
     *
     * @param entity 增加对象命令
     */
    fun add(entity: Entity): ID?

    /**
     * 增加对象
     *
     * @param entities 增加对象命令列表
     */
    fun add(entities: List<Entity>)

    /**
     * 更新对象
     *
     * @param id 对象ID
     * @param entity 更新对象命令
     */
    fun update(id: ID, entity: Entity)

    /**
     * 批量更新对象
     *
     * @param entities 更新对象命令列表
     */
    fun update(entities: List<Entity>)

    /**
     * 增加对象
     * @param id 对象ID
     */
    fun remove(id: ID)
}
