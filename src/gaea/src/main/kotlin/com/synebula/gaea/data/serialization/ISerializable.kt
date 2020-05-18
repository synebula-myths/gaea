package com.synebula.gaea.data.serialization

/**
 * 继承该接口的类都可以序列化对象。
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年9月6日 下午3:42:01
 */
interface ISerializable<T> {
    /**
     * 序列化。
     *
     * @return
     */
    fun serialize(): T
}
