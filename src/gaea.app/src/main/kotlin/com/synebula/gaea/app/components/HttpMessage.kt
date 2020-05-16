package com.synebula.gaea.app.components

import com.synebula.gaea.data.message.Status
import com.synebula.gaea.data.message.Message

class HttpMessage(status: Int = Status.Success) : Message<Any>(status) {
    fun load(msg: Message<*>) {
        this.status = msg.status
        this.message = msg.message
        this.data = msg.data
    }
}