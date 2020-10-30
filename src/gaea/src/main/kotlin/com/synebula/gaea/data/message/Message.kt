package com.synebula.gaea.data.message

open class Message {
    /**
     * 状态。200成功，400错误，500异常
     */
    var status: Int = Status.Success

    /**
     * 获取状态是否成功
     */
    val success: Boolean
        get() = this.status == Status.Success


    /**
     * 附带提示消息
     */
    var message = ""

}