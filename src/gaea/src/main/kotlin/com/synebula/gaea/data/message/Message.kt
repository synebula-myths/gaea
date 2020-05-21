package com.synebula.gaea.data.message

import java.util.*

/**
 *
 * 用来统一Http返回消息类型，通常使用json格式传递
 *
 * @param status http编码。200成功，400错误，500异常
 * @tparam T 消息数据类型
 */
open class Message<T>(var status: Int = Status.Success) {

    /**
     * 获取状态是否成功
     */
    val success: Boolean
        get() = this.status == Status.Success

    /**
     * 传递的业务数据
     */
    var data: T? = null

    /**
     * 附带提示消息
     */
    var message = ""

    /**
     * 消息时间戳
     */
    val timestamp: Long = Date().time

    constructor(data: T) : this(Status.Success) {
        this.data = data
    }

    constructor(status: Int, message: String) : this(status) {
        this.message = message
    }

    constructor(status: Int, data: T, message: String) : this(status) {
        this.data = data
        this.message = message
    }


    /**
     * 从另一对象中复制数据
     *
     * @param other 另一消息对象
     */
    open fun from(other: Message<T>) {
        this.status = other.status
        this.data = other.data
        this.message = other.message
    }
}

