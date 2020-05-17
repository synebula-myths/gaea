package com.synebula.gaea.log.logger

/**
 * ERROR级别日志接口
 * @author Looly
 */
interface IErrorLogger {
    /**
     * @return ERROR 等级是否开启
     */
    val isErrorEnabled: Boolean

    /**
     * 打印 ERROR 等级的日志
     *
     * @param t 错误对象
     */
    fun error(t: Throwable)

    /**
     * 打印 ERROR 等级的日志
     *
     * @param format 消息模板
     * @param args 参数
     */
    fun error(format: String, vararg args: Any)

    /**
     * 打印 ERROR 等级的日志
     *
     * @param t 错误对象
     * @param format 消息模板
     * @param args 参数
     */
    fun error(t: Throwable, format: String, vararg args: Any)

    /**
     * 打印 ERROR 等级的日志
     *
     * @param obj    输出错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun error(obj: Any, format: String, vararg args: Any)

    /**
     * 打印 ERROR 等级的日志
     *
     * @param obj    输出错误对象
     * @param t      错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun error(obj: Any, t: Throwable?, format: String, vararg args: Any)
}
