package com.synebula.gaea.data.serialization.json

/**
 * 序列化器
 */
interface IJsonSerializer {
    /**
     * 序列化
     *
     * @param <S> 源数据类型
     * @param src 源数据
     * @return Json字符串
     */
    fun <S> serialize(src: S): String
}