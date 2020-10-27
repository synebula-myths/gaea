package com.synebula.gaea.domain.service

import com.synebula.gaea.data.message.Message
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.log.ILogger


/**
 * 继承该接口，表明对象为领域服务。
 * @author alex
 * @version 0.0.1
 * @since 2016年9月18日 下午2:23:15
 */
interface IService<TKey> {
    /**
     * 日志组件。
     */
    var logger: ILogger

    fun add(command: ICommand): Message<TKey>

    fun update(id: TKey, command: ICommand)

    fun remove(id: TKey)

    /**
     * 添加一个删除对象前执行监听器。
     * @param key 监听器标志。
     * @param func 监听方法。
     */
    fun addBeforeRemoveListener(key: String, func: (id: TKey) -> Boolean)

    /**
     * 移除一个删除对象前执行监听器。
     * @param key 监听器标志。
     */
    fun removeBeforeRemoveListener(key: String)
}
