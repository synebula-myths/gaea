package com.synebula.gaea.data.message.http

import com.synebula.gaea.data.message.Message

/**
 *
 * 用来统一Http返回消息类型，通常使用json格式传递
 *
 * @param status http编码。200成功，400错误，500异常
 * @tparam T 消息数据类型
 */
class HttpMessage<T>(var status: Int = HttpStatus.Success.code) : Message<T>() {
    /**
     * 附带提示消息
     */
    var message = ""

    constructor(data: T) : this(HttpStatus.Success.code) {
        this.data = data
    }

    constructor(status: Int, message: String) : this(status) {
        this.message = message
    }

    constructor(status: Int, data: T, message: String) : this(status) {
        this.data = data
        this.message = message
    }

    fun from(other: HttpMessage<T>) {
        this.status = other.status
        this.data = other.data
        this.message = message
    }
}

