package com.synebula.gaea.data.message

import com.synebula.gaea.data.serialization.json.IJsonSerializer


class HttpMessage() : DataMessage<Any>() {

    var serializer: IJsonSerializer? = null

    constructor(data: Any, serializer: IJsonSerializer? = null) : this() {
        this.data = data
        this.serializer = serializer
    }

    constructor(status: Int, message: String, serializer: IJsonSerializer? = null) : this() {
        this.status = status
        this.message = message
        this.serializer = serializer
    }

    constructor(status: Int, data: Any, message: String, serializer: IJsonSerializer? = null) : this(
        status,
        message,
        serializer
    ) {
        this.data = data
        this.serializer = serializer
    }

    fun load(msg: DataMessage<*>) {
        this.status = msg.status
        this.message = msg.message
        this.data = msg.data
    }

    override fun toString(): String {
        return serializer?.serialize(this) ?: super.toString()
    }
}