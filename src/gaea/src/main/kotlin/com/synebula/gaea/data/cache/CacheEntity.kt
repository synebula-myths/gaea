package com.synebula.gaea.data.cache

import java.util.*

/**
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年9月29日 下午1:38:58
 */
data class CacheEntity(var key: String, var value: Any) {
    /**
     * 超时时间，单位s
     */
    var overtime: Int = 60
        private set

    /**
     * true代表绝对过期时间，false代表滑动过期。\n
     * 滑动过期是指每次操作都会重新刷新缓存的超时时间。
     */
    var isAbsolute: Boolean = true
        private set
    var timestamp: Long = Date().time
        private set

    constructor(key: String, value: Any, overtime: Int) : this(key, value) {
        this.overtime = overtime
    }

    constructor(key: String, value: Any, overtime: Int, isAbsolute: Boolean) : this(key, value, overtime) {
        this.isAbsolute = isAbsolute
    }

    /**
     * 缓存对象是否过期
     */
    fun isExpired(): Boolean =
            Date().time > this.timestamp + this.overtime * 1000


    /**
     * 刷新缓存时间，仅在滑动过期下有效。
     */
    fun refresh() {
        if (!isAbsolute)
            this.timestamp = Date().time
    }
}