package com.synebula.gaea.record.service

import com.synebula.gaea.bus.IBus
import com.synebula.gaea.data.message.DataMessage
import com.synebula.gaea.db.context.IDbContext
import com.synebula.gaea.domain.event.AfterRemoveEvent
import com.synebula.gaea.domain.event.BeforeRemoveEvent
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.record.model.IRecord


/**
 * 依赖了IRepository仓储借口的服务实现类 GenericsService
 * 该类依赖仓储接口 @see IGenericsRepository, 需要显式提供聚合根的class对象
 *
 * @param context 仓储对象
 * @param clazz 聚合根类对象
 * @param logger 日志组件
 * @author alex
 * @version 0.1
 * @since 2020-05-17
 */
open class Service<TEntity : IRecord<ID>, ID>(
    protected open var clazz: Class<TEntity>,
    protected open var context: IDbContext,
    protected open var bus: IBus<Any>? = null,
    override var logger: ILogger
) : IService<TEntity, ID> {

    override fun add(entity: TEntity): ID? {
        this.context.add(entity, clazz)
        return entity.id
    }

    override fun update(id: ID, entity: TEntity) {
        entity.id = id
        this.context.update(entity, clazz)
    }

    override fun remove(id: ID) {
        val beforeRemoveEvent = BeforeRemoveEvent<TEntity, ID>(id)
        this.bus?.publish(beforeRemoveEvent.topic(this.clazz), beforeRemoveEvent)
        this.context.remove(id, clazz)
        val afterRemoveEvent = AfterRemoveEvent<TEntity, ID>(id)
        this.bus?.publish(afterRemoveEvent.topic(this.clazz), afterRemoveEvent)
    }

    /**
     * 增加对象
     *
     * @param entitys 增加对象命令列表
     */
    override fun add(entitys: List<TEntity>) {
        this.context.add(entitys, clazz)
    }

    /**
     * 批量更新对象
     *
     * @param entitys 更新对象命令列表
     */
    override fun update(entitys: List<TEntity>) {
        this.context.update(entitys, clazz)
    }
}
