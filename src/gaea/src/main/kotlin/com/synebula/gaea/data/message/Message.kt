package com.synebula.gaea.data.message

import java.util.Date

open class Message<T> {
    /**
     * 传递的业务数据
     */
    var data: T? = null

    /**
     * 消息时间戳
     */
    val timestamp: Long = Date().time
}
