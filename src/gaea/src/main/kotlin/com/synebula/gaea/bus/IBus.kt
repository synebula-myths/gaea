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
     * 异常处理
     */
    fun handleException(cause: Throwable?, context: SubscriberExceptionContext<T>)
}