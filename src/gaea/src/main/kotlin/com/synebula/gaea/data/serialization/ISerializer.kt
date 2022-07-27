package com.synebula.gaea.data.serialization

/**
 * 序列化器
 * @param <T> 目标数据类型
 */
interface ISerializer<T> {

    /**
     * 序列化
     * @param <S> 源数据类型
     *
     * @param src 源数据
     * @return 目标数据
     */
    fun <S> serialize(src: S): T
}