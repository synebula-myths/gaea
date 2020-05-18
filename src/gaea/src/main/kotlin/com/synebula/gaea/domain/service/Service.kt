package com.synebula.gaea.domain.service

import com.synebula.gaea.data.IObjectConverter
import com.synebula.gaea.data.message.Message
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
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
open class Service<TAggregateRoot : IAggregateRoot<TKey>, TKey>(
        protected var rootClass: Class<TAggregateRoot>,
        protected var repository: IRepository<TAggregateRoot, TKey>,
        protected var converter: IObjectConverter,
        override var logger: ILogger) : IService<TKey> {

    init {
        this.repository.clazz = rootClass
    }

    override fun add(command: ICommand): Message<TKey> {
        val msg = Message<TKey>()
        val root = this.convert(command)
        this.repository.add(root)
        msg.data = root.id
        return msg
    }

    override fun update(key: TKey, command: ICommand) {
        val root = this.convert(command)
        root.id = key
        this.repository.update(root)
    }

    override fun remove(key: TKey) {
        this.repository.remove(key)
    }

    fun get(key: TKey): TAggregateRoot {
        return this.repository.get(key)
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
