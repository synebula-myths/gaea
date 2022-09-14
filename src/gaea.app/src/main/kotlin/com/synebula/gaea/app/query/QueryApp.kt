package com.synebula.gaea.app.query

import com.synebula.gaea.data.message.HttpMessageFactory
import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQuery
import org.springframework.beans.factory.annotation.Autowired

/**
 * 联合服务，同时实现了ICommandApp和IQueryApp接口
 *
 * @param name 业务名称
 * @param query 业务查询服务
 * @param logger 日志组件
 */
open class QueryApp<TView, ID>(
    override var name: String,
    override var query: IQuery<TView, ID>,
    override var logger: ILogger,
) : IQueryApp<TView, ID> {

    @Autowired
    override lateinit var httpMessageFactory: HttpMessageFactory
}