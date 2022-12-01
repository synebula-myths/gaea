package com.synebula.gaea.app.controller.cmd

import com.synebula.gaea.data.message.HttpMessageFactory
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.service.ISimpleService
import com.synebula.gaea.log.ILogger
import org.springframework.beans.factory.annotation.Autowired

/**
 * 指令服务，同时实现ICommandApp
 *
 * @param name 业务名称
 * @param service 业务domain服务
 * @param logger 日志组件
 */
open class SimpleCommandApp<TRoot : IAggregateRoot<ID>, ID>(
    override var name: String,
    override var service: ISimpleService<TRoot, ID>,
    override var logger: ILogger,
) : ISimpleCommandApp<TRoot, ID> {
    @Autowired
    override lateinit var httpMessageFactory: HttpMessageFactory
}