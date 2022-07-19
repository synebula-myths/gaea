package com.synebula.gaea.app.cmd

import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.service.ILazyService
import com.synebula.gaea.log.ILogger
import javax.annotation.Resource

/**
 * 指令服务，同时实现ICommandApp
 *
 * @param name 业务名称
 * @param service 业务domain服务
 * @param logger 日志组件
 */
open class LazyCommandApp<TRoot : IAggregateRoot<TKey>, TKey>(
    override var name: String,
    override var service: ILazyService<TRoot, TKey>,
    override var logger: ILogger?
) : ILazyCommandApp<TRoot, TKey> {
    @Resource
    override var jsonSerializer: IJsonSerializer? = null
}