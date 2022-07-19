package com.synebula.gaea.log.logger

/**
 * INFO级别日志接口
 */
interface IInfoLogger {
    /**
     * @return INFO 等级是否开启
     */
    val isInfoEnabled: Boolean

    /**
     * 打印 INFO 等级的日志
     *
     * @param t 错误对象
     */
    fun info(t: Throwable)

    /**
     * 打印 INFO 等级的日志
     *
     * @param format 消息模板
     * @param args 参数
     */
    fun info(format: String, vararg args: Any)

    /**
     * 打印 INFO 等级的日志
     *
     * @param t 错误对象
     * @param format 消息模板
     * @param args 参数
     */
    fun info(t: Throwable, format: String, vararg args: Any)

    /**
     * 打印 INFO 等级的日志
     *
     * @param obj    输出错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun info(obj: Any, format: String, vararg args: Any)

    /**
     * 打印 INFO 等级的日志
     *
     * @param obj    输出错误对象
     * @param t      错误对象
     * @param format 消息模板
     * @param args   参数
     */
    fun info(obj: Any, t: Throwable?, format: String, vararg args: Any)
}
