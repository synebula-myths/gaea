package com.synebula.gaea.app.controller

import com.synebula.gaea.app.security.session.UserSession
import com.synebula.gaea.data.message.HttpMessage
import com.synebula.gaea.data.message.HttpMessageFactory
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.log.ILogger
import org.springframework.security.core.context.SecurityContextHolder

interface IApplication {

    /**
     * 业务名称
     */
    var name: String

    /**
     * 日志组件
     */
    var logger: ILogger

    /**
     * 日志详细的构造器
     */
    var httpMessageFactory: HttpMessageFactory


    /**
     * 安全执行
     */
    fun safeExecute(error: String, process: ((msg: HttpMessage) -> Unit)): HttpMessage {
        val msg = this.httpMessageFactory.create()
        try {
            process(msg)
            logger.debug(this, "$name business execute success")
        } catch (ex: Exception) {
            msg.status = Status.ERROR
            msg.message = if (error.isEmpty()) ex.message ?: "" else "$error: ${ex.message}"
            logger.error(this, ex, "[$name]$error: ${ex.message}")
        }
        return msg
    }

    /**
     * 可抛出自定义异常信息的安全controller实现了异常捕获和消息组成。
     */
    fun throwExecute(error: String, process: ((msg: HttpMessage) -> Unit)): HttpMessage {
        val msg = this.httpMessageFactory.create()
        try {
            process(msg)
            logger.debug(this, "$name business execute success")
        } catch (ex: Exception) {
            logger.error(this, ex, "[$name]$error。异常消息将抛出！: ${ex.message}")
            throw RuntimeException(error, ex)
        }
        return msg
    }

    /**
     * 获取用户信息
     */
    fun session(): UserSession? {
        try {
            val authentication = SecurityContextHolder.getContext().authentication.principal
            try {
                return authentication as UserSession
            } catch (ex: Exception) {
                logger.error(this, ex, "[$name]解析用户信息异常！用户信息：$authentication: ${ex.message}")
            }
        } catch (ex: Exception) {
            logger.error(this, ex, "[$name]获取用户信息异常！${ex.message}")
        }
        return null
    }
}