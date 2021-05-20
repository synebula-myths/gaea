package com.synebula.gaea.app.component

import com.synebula.gaea.log.ILogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap

/**
 * 使用 log4j進行日志记录。
 *
 * @author reize
 * @version 0.0.1
 * @since 2016年9月18日 下午2:13:43
 */
@Component
class Logger : ILogger {

    override val name: String
        get() {
            return this.logger.name
        }

    /**
     * 默认日志记录
     */
    private val logger = LoggerFactory.getLogger(Logger::class.java)

    /**
     * 特殊日志记录字典
     */
    private val loggers = ConcurrentHashMap<Class<*>, org.slf4j.Logger>()

    init {
        loggers[Logger::class.java] = logger
    }

    override val isTraceEnabled: Boolean
        get() {
            return this.logger.isTraceEnabled
        }


    override fun trace(t: Throwable) {
        this.logger.trace(t.toString())
    }


    override fun trace(format: String, vararg args: Any) {
        if (this.logger.isTraceEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.trace(message)
        }
    }


    override fun trace(t: Throwable, format: String, vararg args: Any) {
        if (this.logger.isTraceEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.trace(message, t)
        }
    }

    override fun trace(obj: Any, format: String, vararg args: Any) {
        if (this.logger.isTraceEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.trace(message)
        }
    }

    override fun trace(obj: Any, t: Throwable?, format: String, vararg args: Any) {
        if (this.logger.isTraceEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.trace(message, t)
        }
    }

    /**
     * debug start
     */
    override val isDebugEnabled: Boolean
        get() {
            return this.logger.isDebugEnabled
        }


    override fun debug(t: Throwable) {
        if (this.logger.isDebugEnabled) {
            this.logger.debug(t.message, t)
        }
    }


    override fun debug(format: String, vararg args: Any) {
        if (this.logger.isDebugEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.debug(message)
        }
    }


    override fun debug(t: Throwable, format: String, vararg args: Any) {
        if (this.logger.isDebugEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.debug(message, t)
        }
    }

    override fun debug(obj: Any, format: String, vararg args: Any) {
        if (this.logger.isDebugEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.debug(message)
        }
    }

    override fun debug(obj: Any, t: Throwable?, format: String, vararg args: Any) {
        if (this.logger.isDebugEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.debug(message, t)
        }
    }

    /**
     * info start
     */
    override val isInfoEnabled: Boolean
        get() {
            return this.logger.isInfoEnabled
        }

    override fun info(t: Throwable) {
        if (this.logger.isInfoEnabled) {
            this.logger.info(t.message, t)
        }
    }

    override fun info(format: String, vararg args: Any) {
        if (this.logger.isInfoEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.info(message)
        }
    }


    override fun info(t: Throwable, format: String, vararg args: Any) {
        if (this.logger.isInfoEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.info(message, t)
        }
    }

    override fun info(obj: Any, format: String, vararg args: Any) {
        if (this.logger.isInfoEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.info(message)
        }
    }

    override fun info(obj: Any, t: Throwable?, format: String, vararg args: Any) {
        if (this.logger.isInfoEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.info(message, t)
        }
    }

    /**
     * warn start
     */
    override val isWarnEnabled: Boolean
        get() {
            return this.logger.isWarnEnabled
        }

    override fun warn(t: Throwable) {
        if (this.logger.isWarnEnabled) {
            this.logger.warn(t.message, t)
        }
    }

    override fun warn(format: String, vararg args: Any) {
        if (this.logger.isWarnEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.warn(message)
        }
    }


    override fun warn(t: Throwable, format: String, vararg args: Any) {
        if (this.logger.isWarnEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.warn(message, t)
        }
    }

    override fun warn(obj: Any, format: String, vararg args: Any) {
        if (this.logger.isWarnEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.warn(message)
        }
    }

    override fun warn(obj: Any, t: Throwable?, format: String, vararg args: Any) {
        if (this.logger.isWarnEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.warn(message, t)
        }
    }


    /**
     * error start
     */
    override val isErrorEnabled: Boolean
        get() {
            return this.logger.isErrorEnabled
        }

    override fun error(t: Throwable) {
        if (this.logger.isErrorEnabled) {
            this.logger.error(t.message, t)
        }
    }


    override fun error(format: String, vararg args: Any) {
        if (this.logger.isErrorEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.error(message)
        }
    }


    override fun error(t: Throwable, format: String, vararg args: Any) {
        if (this.logger.isErrorEnabled) {
            val message = if (args.isEmpty()) format else String.format(format, *args)
            this.logger.error(message, t)
        }
    }

    override fun error(obj: Any, format: String, vararg args: Any) {
        if (this.logger.isErrorEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.error(message)
        }
    }

    override fun error(obj: Any, t: Throwable?, format: String, vararg args: Any) {
        if (this.logger.isErrorEnabled) {
            val real = this.getLogger(obj)
            val message = if (args.isEmpty()) format else String.format(format, *args)
            real.error(message, t)
        }
    }


    /**
     * 获取日志记录器
     */
    private fun getLogger(obj: Any): org.slf4j.Logger {
        var real = this.loggers.getOrDefault(obj.javaClass, null)
        if (real == null) {
            real = LoggerFactory.getLogger(obj::class.java)
            this.loggers[obj::class.java] = real
        }
        return real!!
    }
}
