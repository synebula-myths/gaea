package com.synebula.gaea.app

import com.google.common.reflect.TypeToken
import com.synebula.gaea.app.cmd.ISimpleCommandApp
import com.synebula.gaea.app.query.IQueryApp
import com.synebula.gaea.data.serialization.json.IJsonSerializer
import com.synebula.gaea.domain.model.AggregateRoot
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.service.ISimpleService
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQuery
import javax.annotation.Resource

/**
 * 简单的服务, 取消了Command对象
 *
 * @param name 业务名称
 * @param clazz 业务对象类型
 * @param service 业务domain服务
 * @param query 业务查询服务
 * @param logger 日志组件
 */
open class SimpleApplication<TRoot : IAggregateRoot<ID>, ID>(
    override var name: String,
    override var clazz: Class<TRoot>,
    override var service: ISimpleService<TRoot, ID>,
    override var query: IQuery,
    override var logger: ILogger,
) : ISimpleCommandApp<TRoot, ID>, IQueryApp<TRoot, ID> {

    @Resource
    override var jsonSerializer: IJsonSerializer? = null
}

fun main() {
    val rawType = TypeToken.of(AggregateRoot::class.java).types.rawTypes()
    println(rawType)
}