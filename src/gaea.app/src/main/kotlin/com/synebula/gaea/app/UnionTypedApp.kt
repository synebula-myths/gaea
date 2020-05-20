package com.synebula.gaea.app

import com.synebula.gaea.app.cmd.ICommandApp
import com.synebula.gaea.app.query.IQueryGenericApp
import com.synebula.gaea.app.query.IQueryTypedApp
import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.domain.service.ICommand
import com.synebula.gaea.domain.service.IService
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.IQueryTyped
import javax.annotation.Resource

/**
 * 联合服务，同时实现了ICommandApp和IQueryApp接口
 *
 * @param name 业务名称
 * @param service 业务domain服务
 * @param query 业务查询服务
 * @param logger 日志组件
 */
open class UnionTypedApp<TCommand : ICommand, TView, TKey>(
        override var name: String,
        override var viewClass: Class<TView>,
        override var service: IService<TKey>?,
        override var query: IQueryTyped?,
        override var logger: ILogger)
    : ICommandApp<TCommand, TKey>, IQueryTypedApp<TView, TKey> {

    @Resource
    override var jsonSerializer: IJsonSerializer? = null
}