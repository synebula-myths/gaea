package com.synebula.gaea.app

import com.synebula.gaea.app.component.HttpMessage
import com.synebula.gaea.data.message.Status
import com.synebula.gaea.log.ILogger

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
     * 安全执行
     */
    fun safeExecute(error: String, process: ((msg: HttpMessage) -> Unit)): HttpMessage {
        val msg = HttpMessage(Status.Success)
        try {
            process(msg)
            logger.debug(this, "$name business execute success")
        } catch (ex: Exception) {
            msg.status = Status.Error
            msg.message = if (error.isEmpty()) ex.message ?: "" else error
            logger.error(this, ex, "$error: ${ex.message}")
        }
        return msg
    }

    /**
     * 可抛出自定义异常信息的安全controller实现了异常捕获和消息组成。
     */
    fun throwExecute(error: String, process: ((msg: HttpMessage) -> Unit)): HttpMessage {
        val msg = HttpMessage(Status.Success)
        try {
            process(msg)
            logger.debug(this, "$name business execute success")
        } catch (ex: Exception) {
            logger.error(this, ex, "$error。异常消息将抛出！: ${ex.message}")
            throw RuntimeException(error, ex)
        }
        return msg
    }
}