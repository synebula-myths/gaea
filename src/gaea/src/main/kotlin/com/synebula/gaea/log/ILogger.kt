package com.synebula.gaea.log

import com.synebula.gaea.log.logger.*

/**
 * 定义日志记录的统一接口。
 *
 * @author  alex
 * @version 0.0.1
 * @since 2016年8月15日 下午3:35:24
 */
interface ILogger : ITraceLogger, IDebugLogger, IInfoLogger, IWarnLogger, IErrorLogger {
    /**
     * @return 日志对象的Name
     */
    val name: String
}
