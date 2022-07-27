package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.DataMessage
import com.synebula.gaea.data.message.StatusMessage
import com.synebula.gaea.data.serialization.IObjectMapper
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.log.ILogger


/**
 * 依赖了IRepository仓储借口的服务实现类 Service
 * 该类依赖仓储接口 @see IRepository, 需要显式提供聚合根的class对象
 *
 * @param repository 仓储对象
 * @param clazz 聚合根类对象
 * @param deserializer 对象转换组件
 * @param logger 日志组件
 * @author alex
 * @version 0.1
 * @since 2020-05-17
 */
open class Service<TAggregateRoot : IAggregateRoot<ID>, ID>(
    protected open var clazz: Class<TAggregateRoot>,
    protected open var repository: IRepository,
    protected open var deserializer: IObjectMapper,
    override var logger: ILogger,
) : IService<ID> {

    /**
     * 删除对象前执行监听器。
     */
    protected val beforeRemoveListeners = mutableMapOf<String, (id: ID) -> StatusMessage>()

    /**
     * 添加一个删除对象前执行监听器。
     * @param key 监听器标志。
     * @param func 监听方法。
     */
    override fun addBeforeRemoveListener(key: String, func: (id: ID) -> StatusMessage) {
        this.beforeRemoveListeners[key] = func
    }

    /**
     * 移除一个删除对象前执行监听器。
     * @param key 监听器标志。
     */
    override fun removeBeforeRemoveListener(key: String) {
        this.beforeRemoveListeners.remove(key)
    }

    override fun add(command: ICommand): DataMessage<ID> {
        val msg = DataMessage<ID>()
        val root = this.deserialize(command)
        this.repository.add(root, this.clazz)
        msg.data = root.id
        return msg
    }

    override fun update(id: ID, command: ICommand) {
        val root = this.deserialize(command)
        root.id = id
        this.repository.update(root, this.clazz)
    }

    override fun remove(id: ID) {
        val functions = this.beforeRemoveListeners.values
        var msg: StatusMessage
        for (func in functions) {
            msg = func(id)
            if (!msg.success()) {
                throw IllegalStateException(msg.message)
            }
        }
        this.repository.remove(id, this.clazz)
    }

    /**
     * 转换ICommand类型到聚合根类型，默认实现，根据需要进行覆写。
     *
     * @param command 需要转换的命令
     * @return 聚合根
     */
    protected open fun deserialize(command: ICommand): TAggregateRoot {
        try {
            return deserializer.deserialize(command, this.clazz)
        } catch (ex: Exception) {
            throw RuntimeException("command not match aggregate root", ex)
        }
    }
}
