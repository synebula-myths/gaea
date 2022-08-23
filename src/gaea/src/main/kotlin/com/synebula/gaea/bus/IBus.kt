package com.synebula.gaea.bus

import java.lang.reflect.Method
import java.util.concurrent.Executor

interface IBus<T : Any> {

    val identifier: String

    val executor: Executor

    /**
     * 注册事件Listener
     * @param subscriber subscriber所在类
     */
    fun register(subscriber: Any)

    /**
     * 取消注册事件Listener
     * @param subscriber Listener所在类
     */
    fun register(subscriber: Any, method: Method)

    /**
     * 取消注册事件Listener
     * @param subscriber Listener所在类
     */
    fun unregister(subscriber: Any)

    /**
     * 同步发布事件
     * @param message 事件
     */
    fun publish(message: T)

    /**
     * 异步发布事件
     * @param message 事件
     */
    fun publishAsync(message: T)

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

    /**
     * 异步发布事件
     * @param topic 主题
     * @param message 事件
     */
    fun publishAsync(topic: String, message: T)

    /**
     * 异常处理
     */
    fun handleException(cause: Throwable?, context: SubscriberExceptionContext<T>)
}