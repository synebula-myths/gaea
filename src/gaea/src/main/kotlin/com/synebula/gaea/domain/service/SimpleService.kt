package com.synebula.gaea.domain.service

import com.synebula.gaea.bus.IBus
import com.synebula.gaea.data.message.DataMessage
import com.synebula.gaea.domain.event.AfterRemoveEvent
import com.synebula.gaea.domain.event.BeforeRemoveEvent
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.log.ILogger


/**
 * 依赖了IRepository仓储借口的服务实现类 GenericsService
 * 该类依赖仓储接口 @see IGenericsRepository, 需要显式提供聚合根的class对象
 *
 * @param repository 仓储对象
 * @param clazz 聚合根类对象
 * @param logger 日志组件
 * @author alex
 * @version 0.1
 * @since 2020-05-17
 */
open class SimpleService<TRoot : IAggregateRoot<ID>, ID>(
    protected open var clazz: Class<TRoot>,
    protected open var repository: IRepository<TRoot, ID>,
    override var logger: ILogger,
    protected open var bus: IBus<Any>? = null
) : ISimpleService<TRoot, ID> {

    override fun add(root: TRoot): DataMessage<ID> {
        val msg = DataMessage<ID>()
        this.repository.add(root)
        msg.data = root.id
        return msg
    }

    override fun update(id: ID, root: TRoot) {
        root.id = id
        this.repository.update(root)
    }

    override fun remove(id: ID) {
        val beforeRemoveEvent = BeforeRemoveEvent<TRoot, ID>(id)
        this.bus?.publish(beforeRemoveEvent.topic(this.clazz), beforeRemoveEvent)
        this.repository.remove(id)
        val afterRemoveEvent = AfterRemoveEvent<TRoot, ID>(id)
        this.bus?.publish(afterRemoveEvent.topic(this.clazz), afterRemoveEvent)
    }

    /**
     * 增加对象
     *
     * @param roots 增加对象命令列表
     */
    override fun add(roots: List<TRoot>) {
        this.repository.add(roots)
    }

    /**
     * 批量更新对象
     *
     * @param roots 更新对象命令列表
     */
    override fun update(roots: List<TRoot>) {
        this.repository.update(roots)
    }
}
