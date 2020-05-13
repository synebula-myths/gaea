package com.synebula.gaea.domain.service

import com.synebula.gaea.data.IObjectConverter
import com.synebula.gaea.data.message.http.HttpMessage
import com.synebula.gaea.domain.model.complex.IComplexAggregateRoot
import com.synebula.gaea.domain.repository.IComplexRepository
import com.synebula.gaea.log.ILogger

/**
 * 复合主键的服务基类
 *
 * @author alex
 * @version 0.1
 * @since 2018 18-2-8
 */
open class ComplexService<TAggregateRoot : IComplexAggregateRoot<TKey, TSecond>, TKey, TSecond>
(var logger: ILogger, protected var repository: IComplexRepository<TAggregateRoot, TKey, TSecond>,
 protected var converter: IObjectConverter, protected var aggregateRootClass: Class<TAggregateRoot>)
    : IComplexService<TKey, TSecond> {

    override fun add(command: ICommand): HttpMessage<Pair<TKey, TSecond>> {
        val msg = HttpMessage<Pair<TKey, TSecond>>()
        val root = this.convert(command)
        repository.add(root)
        msg.data = Pair<TKey, TSecond>(root.id, root.secondary)
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
