package com.synebula.gaea.bus.messagebus

import com.synebula.gaea.bus.IBus
import java.lang.reflect.Method

interface IMessageBus<T : Any> : IBus<T> {

    /**
     * 注册事件Listener
     * @param topics 主题
     * @param subscriber subscriber对象
     */
    fun register(topics: Array<String>, subscriber: Any)

    /**
     * 注册事件Listener
     * @param topics 主题
     * @param subscriber subscriber对象
     * @param method Listener方法
     */
    fun register(topics: Array<String>, subscriber: Any, method: Method)


    /**
     * 取消注册事件Listener
     * @param topic 主题
     * @param subscriber subscriber对象
     */
    fun unregister(topic: String, subscriber: Any)


    /**
     * 同步发布事件
     * @param topic 主题
     * @param message 事件
     */
    fun publish(topic: String, message: T)

}