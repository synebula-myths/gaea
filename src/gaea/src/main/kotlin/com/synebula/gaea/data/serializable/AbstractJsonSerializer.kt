package com.synebula.gaea.data.serializable

abstract class AbstractJsonSerializer : IJsonSerializable {

    protected lateinit var data: Any


    /**
     * 序列化data数据。
     * 实现的serialize方法必须序列化data对象。
     *
     * @param data 需要序列号的数据。
     * @return 序列化后的json数据。
     */
    fun serialize(data: Any): String {
        this.data = data
        return this.serialize()
    }
}
