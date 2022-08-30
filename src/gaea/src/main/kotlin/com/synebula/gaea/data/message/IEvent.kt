package com.synebula.gaea.data.message

interface IEvent {

    /**
     * 获取事件的Topic
     *
     * @param domain 领域名称
     */
    fun topic(domain: String): String {
        return messageTopic(domain, this::class.java)
    }

    /**
     * 获取事件的Topic
     *
     * @param domainClass 领域类
     */
    fun topic(domainClass: Class<*>): String {
        return messageTopic(domainClass, this::class.java)
    }
}