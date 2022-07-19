package com.synebula.gaea.app.component.bus

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import com.synebula.gaea.bus.IMessage
import com.synebula.gaea.bus.IMessageBus
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class EventBus : IMessageBus {

    /**
     * 同步事件总线
     */
    var eventBus = EventBus()

    /**
     * 异步事件总线
     */
    var asyncEventBus = AsyncEventBus(Executors.newFixedThreadPool(2))

    override fun register(obj: Any) {
        eventBus.register(obj)
        asyncEventBus.register(obj)
    }

    override fun unregister(obj: Any) {
        eventBus.unregister(obj)
        asyncEventBus.unregister(obj)
    }

    override fun publish(message: IMessage) {
        eventBus.post(message)
    }

    override fun publishAsync(message: IMessage) {
        asyncEventBus.post(message)
    }

}

