package com.synebula.gaea.data.message

import com.synebula.gaea.ext.firstCharLowerCase

/**
 * 获取事件的Topic
 *
 * @param domain 领域名称
 * @param messageClass 消息的类型
 */
fun messageTopic(domain: String, messageClass: Class<*>): String {
    return if (domain.isNotBlank()) "$domain.${messageClass.simpleName}" else messageClass.simpleName
}

/**
 * 获取事件的Topic
 *
 * @param domainClass 领域类
 * @param messageClass 消息的类型
 */
fun messageTopic(domainClass: Class<*>, messageClass: Class<*>): String {
    return "${domainClass.simpleName.firstCharLowerCase()}.${messageClass.simpleName}"
}