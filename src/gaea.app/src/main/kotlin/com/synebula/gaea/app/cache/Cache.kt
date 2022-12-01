package com.synebula.gaea.app.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.synebula.gaea.data.cache.ICache
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit

/**
 * 缓存组件
 *
 * @param expire 滑动过期时间，单位秒
 */
open class Cache<K, V>(expire: Int) : ICache<K, V> {

    private val guavaCache: Cache<K, V>

    init {
        var builder = CacheBuilder.newBuilder()
        if (expire != 0) {
            builder = builder.expireAfterAccess(expire.toLong(), TimeUnit.SECONDS)
        }
        this.guavaCache = builder.build()
    }

    override fun add(key: K, value: V) {
        this.guavaCache.put(key, value)
    }

    /**
     * 添加多条缓存记录
     *
     * @param entries 需要添加的多条缓存记录Map
     */
    override fun add(entries: Map<K, V>) {
        this.guavaCache.putAll(entries)
    }

    override fun get(key: K): V? {
        return this.guavaCache.getIfPresent(key ?: "")
    }

    /**
     * 获取给出列表key中存在的缓存元素
     *
     * @param keys 尝试获取的key列表
     */
    override fun get(keys: Iterable<K>): Map<K, V> {
        return this.guavaCache.getAllPresent(keys)
    }


    /**
     * 清除给出key的缓存
     *
     * @param key 需要清楚的缓存key
     */
    override fun remove(key: K) {
        this.guavaCache.invalidate(key ?: "")
    }

    /**
     * 清除给出列表key的缓存
     *
     * @param keys 需要清楚的缓存key列表
     */
    override fun remove(keys: Iterable<K>) {
        this.guavaCache.invalidateAll(keys)
    }

    /**
     * 判断keys是否存在
     *
     * @param keys 需要判断的缓存key列表
     */
    override fun exists(keys: Iterable<K>): Map<K, Boolean> {
        val result = mutableMapOf<K, Boolean>()
        keys.forEach { result[it] = guavaCache.asMap().containsKey(it) }
        return result
    }

    /**
     * 判断key是否存在
     *
     * @param key 需要判断的key
     */
    override fun exists(key: K): Boolean {
        return this.guavaCache.asMap().containsKey(key)
    }

    /**
     * 清除所有缓存记录
     */
    override fun clear() {
        this.guavaCache.cleanUp()
    }

    fun asMap(): ConcurrentMap<K, V> {
        return this.guavaCache.asMap()
    }
}