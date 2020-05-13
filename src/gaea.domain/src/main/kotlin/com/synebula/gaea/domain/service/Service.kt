package com.synebula.gaea.domain.service

import com.synebula.gaea.data.IObjectConverter
import com.synebula.gaea.data.message.http.HttpMessage
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import com.synebula.gaea.log.ILogger


/**
 * class FlatService
 *
 * @author alex
 * @version 0.1
 * @since 2018 18-2-8
 */
open class Service<TAggregateRoot : IAggregateRoot<TKey>, TKey>
(override var logger: ILogger,
 protected var repository: IRepository<TAggregateRoot, TKey>,
 protected var converter: IObjectConverter,
 protected var aggregateRootClass: Class<TAggregateRoot>) : IService<TKey> {

    override fun add(command: ICommand): HttpMessage<TKey> {
        val msg = HttpMessage<TKey>()
        val root = this.convert(command)
        repository.add(root)
        msg.data = root.id
        return msg
    }

    override fun update(key: TKey, command: ICommand) {
        val root = this.convert(command)
        root.id = key
        repository.update(root)
    }

    override fun remove(key: TKey) {
        repository.remove(key)
    }

    /**
     * 转换ICommand类型到聚合根类型，默认实现，根据需要进行覆写。
     *
     * @param command
     * @return
     */
    protected fun convert(command: ICommand): TAggregateRoot {
        try {
            return converter.convert(command, aggregateRootClass)
        } catch (ex: Exception) {
            throw RuntimeException("command not match aggregate root", ex)
        }

    }
}
