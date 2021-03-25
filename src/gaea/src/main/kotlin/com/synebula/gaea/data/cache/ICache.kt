package com.synebula.gaea.data.cache

/**
 * 缓存接口。
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年8月15日 下午4:53:19
 */
interface ICache {
    fun add(key: String, value: CacheEntity)

    operator fun get(key: String): CacheEntity?
}
