package com.synebula.gaea.bus

interface IMessageBus {

    /**
     * 注册事件Listener
     * @param obj Listener所在类
     */
    fun register(obj: Any)

    /**
     * 取消注册事件Listener
     * @param obj Listener所在类
     */
    fun unregister(obj: Any)

    /**
     * 同步发布事件
     * @param message 事件
     */
    fun publish(message: IMessage)

    /**
     * 异步发布事件
     * @param message 事件
     */
    fun publishAsync(message: IMessage)
}