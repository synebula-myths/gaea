package com.synebula.gaea.log.logger

/**
 * DEBUG级别日志接口
 *
 * @author Looly
 */
interface IDebugLogger {
    /**
     * @return DEBUG 等级是否开启
     */
    val isDebugEnabled: Boolean

    /**
     * 打印 DEBUG 等级的日志
     *
     * @param t 错误对象
     */
    fun debug(t: Throwable)

    /**
     * 打印 DEBUG 等级的日志
     *
     * @param format 消息模板
     * @param args   参数
     */
    fun debug(format: String, vararg args: Any)

    /**
     * 打印 DEBUG 等级的日志
     *
     * @param t      错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun debug(t: Throwable, format: String, vararg args: Any)

    /**
     * 打印 DEBUG 等级的日志
     *
     * @param obj    输出错误对象
     * @param t      错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun debug(obj: Any, t: Throwable, format: String, vararg args: Any)
}
