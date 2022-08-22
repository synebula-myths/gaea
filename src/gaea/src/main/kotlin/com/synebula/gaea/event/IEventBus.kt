package com.synebula.gaea.event

interface IEventBus {

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
     * @param event 事件
     */
    fun publish(event: IEvent)

    /**
     * 异步发布事件
     * @param event 事件
     */
    fun publishAsync(event: IEvent)
}