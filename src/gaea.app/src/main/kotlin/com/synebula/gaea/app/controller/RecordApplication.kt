package com.synebula.gaea.app.controller

import com.synebula.gaea.app.controller.cmd.ISimpleCommandApp
import com.synebula.gaea.app.controller.query.IQueryApp
import com.synebula.gaea.data.message.HttpMessageFactory
import com.synebula.gaea.db.query.IQuery
import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.record.service.IService
import org.springframework.beans.factory.annotation.Autowired

/**
 * 简单的服务, 取消了Command对象
 *
 * @param name 业务名称
 * @param service 业务domain服务
 * @param query 业务查询服务
 * @param logger 日志组件
 */
open class RecordApplication<TRoot : IAggregateRoot<ID>, ID>(
    override var name: String,
    override var service: IService<TRoot, ID>,
    override var query: IQuery,
    override var clazz: Class<TRoot>,
    override var logger: ILogger,
) : ISimpleCommandApp<TRoot, ID>, IQueryApp<TRoot, ID> {

    @Autowired
    override lateinit var httpMessageFactory: HttpMessageFactory
}