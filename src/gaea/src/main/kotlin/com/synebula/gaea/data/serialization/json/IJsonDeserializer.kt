package com.synebula.gaea.data.serialization.json

/**
 * 序列化器
 */
interface IJsonDeserializer {
    /**
     * 反序列化
     *
     * @param <T> 目标数据类型
     * @param src Json字符串数据
     * @return 目标数据
     */
    fun <T> deserialize(src: String): T
}