package com.synebula.gaea.app

import com.synebula.gaea.app.cmd.ILazyCommandApp
import com.synebula.gaea.app.query.IQueryApp
import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.service.ILazyService
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQuery
import javax.annotation.Resource

/**
 * 联合服务，同时实现了ILazyCommandApp和IQueryApp接口
 *
 * @param name 业务名称
 * @param service 业务domain服务
 * @param query 业务查询服务
 * @param logger 日志组件
 */
open class LazyApplication<TRoot : IAggregateRoot<TKey>, TKey>(
    override var name: String,
    override var clazz: Class<TRoot>, //view class type
    override var service: ILazyService<TRoot, TKey>,
    override var query: IQuery,
    override var logger: ILogger,
) : ILazyCommandApp<TRoot, TKey>, IQueryApp<TRoot, TKey> {

    @Resource
    override var jsonSerializer: IJsonSerializer? = null
}