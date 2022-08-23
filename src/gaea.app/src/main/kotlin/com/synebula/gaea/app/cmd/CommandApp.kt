package com.synebula.gaea.app.cmd

import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.domain.service.ICommand
import com.synebula.gaea.domain.service.IService
import com.synebula.gaea.log.ILogger
import javax.annotation.Resource

/**
 * 指令服务，同时实现ICommandApp
 *
 * @param name 业务名称
 * @param service 业务domain服务
 * @param logger 日志组件
 */
open class CommandApp<TCommand : ICommand, ID>(
    override var name: String,
    override var service: IService<ID>,
    override var logger: ILogger,
) : ICommandApp<TCommand, ID> {
    @Resource
    override var jsonSerializer: IJsonSerializer? = null
}