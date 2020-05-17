package com.synebula.gaea.domain.service

import com.synebula.gaea.data.IObjectConverter
import com.synebula.gaea.data.message.Message
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepositoryTyped
import com.synebula.gaea.log.ILogger


/**
 * class FlatService
 *
 * @param repository 仓储对象
 * @param rootClass 聚合根类对象
 * @param converter 对象转换组件
 * @param logger 日志组件
 * @author alex
 * @version 0.1
 * @since 2018 18-2-8
 */
open class ServiceTyped<TAggregateRoot : IAggregateRoot<TKey>, TKey>(
        protected var rootClass: Class<TAggregateRoot>,
        protected var repository: IRepositoryTyped,
        protected var converter: IObjectConverter,
        override var logger: ILogger) : IService<TKey> {

    override fun add(command: ICommand): Message<TKey> {
        val msg = Message<TKey>()
        val root = this.convert(command)
        this.repository.add(root, rootClass)
        msg.data = root.id
        return msg
    }

    override fun update(key: TKey, command: ICommand) {
        val root = this.convert(command)
        root.id = key
        this.repository.update(root, rootClass)
    }

    override fun remove(key: TKey) {
        this.repository.remove(key, rootClass)
    }

    override fun <TAggregateRoot : IAggregateRoot<TKey>> get(key: TKey, clazz: Class<TAggregateRoot>): TAggregateRoot {
        return this.repository.get(key, clazz)
    }

    /**
     * 转换ICommand类型到聚合根类型，默认实现，根据需要进行覆写。
     *
     * @param command
     * @return
     */
    protected fun convert(command: ICommand): TAggregateRoot {
        try {
            return converter.convert(command, rootClass)
        } catch (ex: Exception) {
            throw RuntimeException("command not match aggregate root", ex)
        }
    }
}
