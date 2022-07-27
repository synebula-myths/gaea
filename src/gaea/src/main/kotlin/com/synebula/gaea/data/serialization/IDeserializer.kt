package com.synebula.gaea.data.serialization

/**
 * 序列化器
 * @param <S> 源数据类型
 */
interface IDeserializer<S> {

    /**
     * 反序列化
     *
     * @param <T> 目标数据类型
     * @param src 源数据
     * @param targetClass 目标对象。
     * @return 目标数据
     */
    fun <T> deserialize(src: S, targetClass: Class<T>): T
}