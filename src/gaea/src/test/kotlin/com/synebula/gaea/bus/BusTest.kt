package com.synebula.gaea.bus

import com.synebula.gaea.bus.messagebus.AsyncMessageBus
import com.synebula.gaea.bus.messagebus.IMessageBus
import com.synebula.gaea.bus.messagebus.MessageBus
import org.junit.Test
import java.util.concurrent.Executors


class BusTest {

    @Test
    fun testBus() {
        val bus: IBus<Any> = Bus()
        val subscriber = TestSubscriber()
        bus.register(subscriber)
        bus.publish("Hello world")
        bus.publish(subscriber)
        bus.publish(1)
    }

    @Test
    fun testAsyncBus() {
        val bus: IBus<Any> = AsyncBus(Executors.newFixedThreadPool(10))
        val subscriber = TestSubscriber()
        bus.register(subscriber, subscriber.javaClass.declaredMethods[0])
        bus.register(subscriber, subscriber.javaClass.declaredMethods[1])
        bus.publish("Hello world")
        bus.publish(subscriber)
    }

    @Test
    fun testMessageBus() {
        val bus: IMessageBus<Any> = MessageBus()
        val subscriber = TestTopicSubscriber()
        bus.register(subscriber)
        bus.publish("hello", "Hello world")
        bus.publish("whoami", subscriber)
    }

    @Test
    fun testMessageBus2() {
        val bus: IMessageBus<Any> = AsyncMessageBus(Executors.newFixedThreadPool(10))
        val subscriber = TestTopicSubscriber()
        val subscriber2 = TestTopicSubscriber2()
        bus.register(subscriber)
        bus.register(subscriber2)
        bus.publish("hello", "Hello world")
        bus.publish("whoami", subscriber)

        bus.unregister(subscriber2)
        bus.publish("hello", "Hello world")
        bus.publish("whoami", subscriber)
    }

    internal class TestSubscriber {
        @Subscribe
        fun echo(name: String) {
            println(name)
        }

        @Subscribe
        fun whoAmI(testSubscriber: TestSubscriber) {
            println(testSubscriber.javaClass.name)
        }

    }

    internal class TestTopicSubscriber {
        @Subscribe(topics = ["hello"])
        fun echo(name: String) {
            println(name)
        }

        @Subscribe(topics = ["whoami"])
        fun whoAmI(testSubscriber: TestTopicSubscriber) {
            println(testSubscriber.javaClass.name)
        }

    }

    internal class TestTopicSubscriber2 {
        @Subscribe(topics = ["hello"])
        fun echo(name: String) {
            println("2 $name")
        }

        @Subscribe(topics = ["whoami"])
        fun whoAmI(testSubscriber: TestTopicSubscriber) {
            println("2 ${testSubscriber.javaClass.name}")
        }

    }
}