package com.synebula.gaea.data.message

import java.util.*

open class StatusMessage : IMessage {
    /**
     * 状态。200成功，400错误，500异常
     */
    var status: Int = Status.SUCCESS

    /**
     * 获取状态是否成功
     */
    fun success(): Boolean = this.status == Status.SUCCESS

    /**
     * 附带提示消息
     */
    override var message = ""

    /**
     * 时间戳。
     */
    override var timestamp = Date().time
}