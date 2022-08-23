package com.synebula.gaea.log.logger

/**
 * TRACE级别日志接口
 *
 */
interface ITraceLogger {
    /**
     * @return TRACE 等级是否开启
     */
    val isTraceEnabled: Boolean

    /**
     * 打印 TRACE 等级的日志
     *
     * @param t 错误对象
     */
    fun trace(t: Throwable)

    /**
     * 打印 TRACE 等级的日志
     *
     * @param format 消息模板
     * @param args   参数
     */
    fun trace(format: String, vararg args: Any)

    /**
     * 打印 TRACE 等级的日志
     *
     * @param t      错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun trace(t: Throwable, format: String, vararg args: Any)

    /**
     * 打印 TRACE 等级的日志
     *
     * @param obj    输出错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun trace(obj: Any, format: String, vararg args: Any)


    /**
     * 打印 TRACE 等级的日志
     *
     * @param obj    输出错误对象
     * @param t      错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun trace(obj: Any, t: Throwable?, format: String, vararg args: Any)

}
