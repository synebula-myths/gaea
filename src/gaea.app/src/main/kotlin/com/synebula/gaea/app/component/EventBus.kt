package com.synebula.gaea.app.component

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import com.synebula.gaea.event.IEvent
import com.synebula.gaea.event.IEventBus
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class EventBus : IEventBus {

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

    override fun publish(event: IEvent) {
        eventBus.post(event)
    }

    override fun publishAsync(event: IEvent) {
        asyncEventBus.post(event)
    }

}

