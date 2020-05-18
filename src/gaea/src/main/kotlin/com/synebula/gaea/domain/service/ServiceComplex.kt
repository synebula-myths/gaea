package com.synebula.gaea.domain.service

import com.synebula.gaea.data.IObjectConverter
import com.synebula.gaea.data.message.Message
import com.synebula.gaea.domain.model.complex.IComplexAggregateRoot
import com.synebula.gaea.domain.repository.IRepositoryComplex
import com.synebula.gaea.log.ILogger

/**
 * 复合主键的服务基类
 *
 * @author alex
 * @version 0.1
 * @since 2020-05-15
 */
open class ServiceComplex<TAggregateRoot : IComplexAggregateRoot<TKey, TSecond>, TKey, TSecond>
(var logger: ILogger, protected var repository: IRepositoryComplex<TAggregateRoot, TKey, TSecond>,
 protected var converter: IObjectConverter, protected var aggregateRootClass: Class<TAggregateRoot>)
    : IServiceComplex<TKey, TSecond> {

    override fun add(command: ICommand): Message<Pair<TKey, TSecond>> {
        val msg = Message<Pair<TKey, TSecond>>()
        val root = this.convert(command)
        repository.add(root)
        msg.data = Pair<TKey, TSecond>(root.id!!, root.secondary!!)
        return msg
    }

    override fun update(key: TKey, secondary: TSecond, command: ICommand) {
        val root = this.convert(command)
        root.id = key
        root.secondary = secondary
        repository.update(root)
    }

    override fun remove(key: TKey, secondary: TSecond) {
        repository.remove(key, secondary)
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
