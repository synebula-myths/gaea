package com.synebula.gaea.app.component

import com.synebula.gaea.data.message.Status
import com.synebula.gaea.data.message.Message

class HttpMessage() : Message<Any>() {

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

    fun load(msg: Message<*>) {
        this.status = msg.status
        this.message = msg.message
        this.data = msg.data
    }
}