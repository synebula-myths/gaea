package com.synebula.gaea.log.logger

/**
 * WARN级别日志接口
 */
interface IWarnLogger {
    /**
     * @return WARN 等级是否开启
     */
    val isWarnEnabled: Boolean

    /**
     * 打印 WARN 等级的日志
     *
     * @param t 错误对象
     */
    fun warn(t: Throwable)

    /**
     * 打印 WARN 等级的日志
     *
     * @param format 消息模板
     * @param args 参数
     */
    fun warn(format: String, vararg args: Any)

    /**
     * 打印 WARN 等级的日志
     *
     * @param t 错误对象
     * @param format 消息模板
     * @param args 参数
     */
    fun warn(t: Throwable, format: String, vararg args: Any)


    /**
     * 打印 WARN 等级的日志
     *
     * @param obj 输出错误对象
     * @param format 消息模板
     * @param args 参数
     */
    fun warn(obj: Any, format: String, vararg args: Any)

    /**
     * 打印 WARN 等级的日志
     *
     * @param obj    输出错误对象
     * @param t      错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun warn(obj: Any, t: Throwable?, format: String, vararg args: Any)
}
