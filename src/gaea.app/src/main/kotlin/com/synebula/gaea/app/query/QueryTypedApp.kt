package com.synebula.gaea.app.query

import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQuery

/**
 * 联合服务，同时实现了ICommandApp和IQueryApp接口
 *
 * @param name 业务名称
 * @param query 业务查询服务
 * @param logger 日志组件
 */
open class QueryTypedApp<TView, TKey>(
    override var name: String,
    override var clazz: Class<TView>,
    override var query: IQuery?,
    override var logger: ILogger) : IQueryTypedApp<TView, TKey> {
}
