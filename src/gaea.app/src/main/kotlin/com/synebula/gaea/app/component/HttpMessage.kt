package com.synebula.gaea.app.component

import com.synebula.gaea.data.message.DataMessage

class HttpMessage() : DataMessage<Any>() {

    constructor(data: Any) : this() {
        this.data = data
    }

    constructor(status: Int, message: String) : this() {
        this.status = status
        this.message = message
    }

    constructor(status: Int, data: Any, message: String) : this(status, message) {
        this.data = data
    }

    fun load(msg: DataMessage<*>) {
        this.status = msg.status
        this.message = msg.message
        this.data = msg.data
    }
}