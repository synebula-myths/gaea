package com.synebula.gaea.data.message

import com.synebula.gaea.data.serialization.json.IJsonSerializer

class HttpMessageFactory(private var serializer: IJsonSerializer) {

    fun create(): HttpMessage {
        return HttpMessage(serializer)
    }

    fun create(data: Any): HttpMessage {
        return HttpMessage(data, serializer)
    }

    fun create(status: Int, message: String): HttpMessage {
        return HttpMessage(status, message, serializer)
    }

    fun create(status: Int, data: Any, message: String): HttpMessage {
        return HttpMessage(status, data, message, serializer)
    }
}