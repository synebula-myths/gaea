package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.Message
import com.synebula.gaea.log.ILogger


/**
 * 继承该接口，表明对象为领域服务。
 * @author alex
 * @version 0.0.1
 * @since 2016年9月18日 下午2:23:15
 */
interface IService<TKey> {
    /**
     * 日志组件。若无日志组件则默认为NullLogger对象，不会为null。
     */
    var logger: ILogger

    fun add(command: ICommand): Message<TKey>

    fun update(key: TKey, command: ICommand)

    fun remove(key: TKey)
}
