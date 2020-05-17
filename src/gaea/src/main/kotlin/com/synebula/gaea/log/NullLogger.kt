package com.synebula.gaea.log

/**
 * 默认空日志对象，不会执行任何操作。可以让使用日志的时候可以不会为空（null）。
 *
 * @author  alex
 * @version 0.0.1
 * @since 2016年9月18日 下午1:58:36
 */
class NullLogger : ILogger {

    override val isTraceEnabled: Boolean
        get() = false

    override val isDebugEnabled: Boolean
        get() = false

    override val isInfoEnabled: Boolean
        get() = false

    override val isWarnEnabled: Boolean
        get() = false

    override val isErrorEnabled: Boolean
        get() = false

    override val name: String
        get() = NullLogger::class.simpleName!!

    override fun trace(t: Throwable) {

    }

    override fun trace(format: String, vararg args: Any) {

    }

    override fun trace(t: Throwable, format: String, vararg args: Any) {

    }

    override fun trace(obj: Any, format: String, vararg args: Any) {

    }

    override fun trace(obj: Any, t: Throwable?, format: String, vararg args: Any) {

    }

    override fun debug(t: Throwable) {

    }

    override fun debug(format: String, vararg args: Any) {

    }

    override fun debug(t: Throwable, format: String, vararg args: Any) {

    }

    override fun debug(obj: Any, format: String, vararg args: Any) {

    }

    override fun debug(obj: Any, t: Throwable?, format: String, vararg args: Any) {

    }

    override fun info(t: Throwable) {

    }

    override fun info(format: String, vararg args: Any) {

    }

    override fun info(t: Throwable, format: String, vararg args: Any) {

    }

    override fun info(obj: Any, format: String, vararg args: Any) {

    }

    override fun info(obj: Any, t: Throwable?, format: String, vararg args: Any) {

    }

    override fun warn(t: Throwable) {

    }

    override fun warn(format: String, vararg args: Any) {

    }

    override fun warn(t: Throwable, format: String, vararg args: Any) {

    }

    override fun warn(obj: Any, format: String, vararg args: Any) {

    }

    override fun warn(obj: Any, t: Throwable?, format: String, vararg args: Any) {

    }

    override fun error(t: Throwable) {

    }

    override fun error(format: String, vararg args: Any) {

    }

    override fun error(t: Throwable, format: String, vararg args: Any) {

    }

    override fun error(obj: Any, format: String, vararg args: Any) {

    }

    override fun error(obj: Any, t: Throwable?, format: String, vararg args: Any) {

    }
}
