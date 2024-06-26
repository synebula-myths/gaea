package com.synebula.gaea.data.message

/**
 * 消息结构
 */
interface IMessage {
    /**
     * 消息载荷, 实际的业务数据
     */
    var message: String

    /**
     * 时间戳。
     */
    var timestamp: Long

    /**
     * 获取消息的Topic
     *
     * @param domain 领域名称
     */
    fun topic(domain: String): String {
        return messageTopic(domain, this::class.java)
    }

    /**
     * 获取消息的Topic
     *
     * @param domainClass 领域类
     */
    fun topic(domainClass: Class<*>): String {
        return messageTopic(domainClass, this::class.java)
    }
}