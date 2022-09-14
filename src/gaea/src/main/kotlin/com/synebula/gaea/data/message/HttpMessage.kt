package com.synebula.gaea.data.message

import com.synebula.gaea.data.serialization.json.IJsonSerializer


class HttpMessage(private var serializer: IJsonSerializer) : DataMessage<Any>() {


    constructor(data: Any, serializer: IJsonSerializer) : this(serializer) {
        this.data = data
    }

    constructor(status: Int, message: String, serializer: IJsonSerializer) : this(serializer) {
        this.status = status
        this.message = message
    }

    constructor(status: Int, data: Any, message: String, serializer: IJsonSerializer) : this(
        status,
        message,
        serializer
    ) {
        this.data = data
    }

    fun load(msg: DataMessage<*>) {
        this.status = msg.status
        this.message = msg.message
        this.data = msg.data
    }

    override fun toString(): String {
        return serializer.serialize(this)
    }
}