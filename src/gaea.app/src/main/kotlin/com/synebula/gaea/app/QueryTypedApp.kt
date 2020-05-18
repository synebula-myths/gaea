package com.synebula.gaea.app

import com.synebula.gaea.log.ILogger
import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.IQueryTyped

/**
 * 联合服务，同时实现了ICommandApp和IQueryApp接口
 *
 * @param name 业务名称
 * @param query 业务查询服务
 * @param logger 日志组件
 */
open class QueryTypedApp<TView, TKey>(
        override var name: String,
        override var viewClass: Class<TView>,
        override var query: IQueryTyped?,
        override var logger: ILogger) : IQueryTypedApp<TView, TKey> {
}
