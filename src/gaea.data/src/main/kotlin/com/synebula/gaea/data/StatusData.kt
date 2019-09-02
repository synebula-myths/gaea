package com.synebula.gaea.data

import com.synebula.gaea.data.type.Status
import java.util.*

/**
 *
 * @author reize
 * @version 0.0.1
 * @since 2016年9月6日 下午3:47:35
 */
open class StatusData<T> {
    var code: Status
        set(value) {
            field = value
            this.status = value.code
        }
    var status: Int
    var message: String = ""
    var data: T? = null
    val timestamp = Date().time

    /**
     * ctor
     */
    constructor(code: Status) {
        this.code = code
        this.status = code.code
    }

    constructor() : this(Status.success) {
    }

    /**
     * @param data 数据
     */
    constructor(data: T) : this(Status.success) {
        this.data = data
    }

    /**
     * @param status 状态
     * @param message 消息
     */
    constructor(status: Status, message: String) : this(status) {
        this.message = message
    }

    /**
     * @param status 状态
     * @param message 消息
     * @param data 数据
     */
    constructor(status: Status, message: String, data: T) : this(status) {
        this.message = message
        this.data = data
    }
}