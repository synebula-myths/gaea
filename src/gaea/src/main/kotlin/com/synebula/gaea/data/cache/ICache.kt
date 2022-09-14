package com.synebula.gaea.data.cache

/**
 * 缓存接口。
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年8月15日 下午4:53:19
 */
interface ICache<K, V> {

    /**
     * 添加一条缓存记录
     *
     * @param key 缓存key
     * @param value 需要缓存的值
     */
    fun add(key: K, value: V)

    /**
     * 添加多条缓存记录
     *
     * @param entries 需要添加的多条缓存记录Map
     */
    fun add(entries: Map<K, V>)

    /**
     * 尝试获取缓存记录值
     *
     * @param key 缓存key
     */
    operator fun get(key: K): V?

    /**
     * 获取给出列表key中存在的缓存元素
     *
     * @param keys 尝试获取的key列表
     */
    fun get(keys: Iterable<K>): Map<K, V>

    /**
     * 清除所有缓存记录
     */
    fun clear()


    /**
     * 清除给出key的缓存
     *
     * @param key 需要清楚的缓存key
     */
    fun remove(key: K)

    /**
     * 清除给出列表key的缓存
     *
     * @param keys 需要清楚的缓存key列表
     */
    fun remove(keys: Iterable<K>)

    /**
     * 判断key是否存在
     *
     * @param key 需要判断的key
     */
    fun exists(key: K): Boolean

    /**
     * 判断keys是否存在
     *
     * @param keys 需要判断的缓存key列表
     */
    fun exists(keys: Iterable<K>): Map<K, Boolean>
}
