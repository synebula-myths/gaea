/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.synebula.gaea.bus.messagebus

import com.synebula.gaea.bus.*
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.Executor
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Dispatches events to listeners, and provides ways for listeners to register themselves.
 *
 * <h2>Avoid EventBus</h2>
 *
 *
 * **We recommend against using EventBus.** It was designed many years ago, and newer
 * libraries offer better ways to decouple components and react to events.
 *
 *
 * To decouple components, we recommend a dependency-injection framework. For Android code, most
 * apps use [Dagger](https://dagger.dev). For server code, common options include [Guice](https://github.com/google/guice/wiki/Motivation) and [Spring](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-introduction).
 * Frameworks typically offer a way to register multiple listeners independently and then request
 * them together as a set ([Dagger](https://dagger.dev/dev-guide/multibindings), [Guice](https://github.com/google/guice/wiki/Multibindings), [Spring](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-autowired-annotation)).
 *
 *
 * To react to events, we recommend a reactive-streams framework like [RxJava](https://github.com/ReactiveX/RxJava/wiki) (supplemented with its [RxAndroid](https://github.com/ReactiveX/RxAndroid) extension if you are building for
 * Android) or [Project Reactor](https://projectreactor.io/). (For the basics of
 * translating code from using an event bus to using a reactive-streams framework, see these two
 * guides: [1](https://blog.jkl.gg/implementing-an-event-bus-with-rxjava-rxbus/), [2](https://lorentzos.com/rxjava-as-event-bus-the-right-way-10a36bdd49ba).) Some usages
 * of EventBus may be better written using [Kotlin coroutines](https://kotlinlang.org/docs/coroutines-guide.html), including [Flow](https://kotlinlang.org/docs/flow.html) and [Channels](https://kotlinlang.org/docs/channels.html). Yet other usages are better served
 * by individual libraries that provide specialized support for particular use cases.
 *
 *
 * Disadvantages of EventBus include:
 *
 *
 *  * It makes the cross-references between producer and subscriber harder to find. This can
 * complicate debugging, lead to unintentional reentrant calls, and force apps to eagerly
 * initialize all possible subscribers at startup time.
 *  * It uses reflection in ways that break when code is processed by optimizers/minimizer like
 * [R8 and Proguard](https://developer.android.com/studio/build/shrink-code).
 *  * It doesn't offer a way to wait for multiple events before taking action. For example, it
 * doesn't offer a way to wait for multiple producers to all report that they're "ready," nor
 * does it offer a way to batch multiple events from a single producer together.
 *  * It doesn't support backpressure and other features needed for resilience.
 *  * It doesn't provide much control of threading.
 *  * It doesn't offer much monitoring.
 *  * It doesn't propagate exceptions, so apps don't have a way to react to them.
 *  * It doesn't interoperate well with RxJava, coroutines, and other more commonly used
 * alternatives.
 *  * It imposes requirements on the lifecycle of its subscribers. For example, if an event
 * occurs between when one subscriber is removed and the next subscriber is added, the event
 * is dropped.
 *  * Its performance is suboptimal, especially under Android.
 *  * It [doesn't support parameterized
 * types](https://github.com/google/guava/issues/1431).
 *  * With the introduction of lambdas in Java 8, EventBus went from less verbose than listeners
 * to [more verbose](https://github.com/google/guava/issues/3311).
 *
 *
 * <h2>EventBus Summary</h2>
 *
 *
 * The EventBus allows publish-subscribe-style communication between components without requiring
 * the components to explicitly register with one another (and thus be aware of each other). It is
 * designed exclusively to replace traditional Java in-process event distribution using explicit
 * registration. It is *not* a general-purpose publish-subscribe system, nor is it intended
 * for interprocess communication.
 *
 * <h2>Receiving Events</h2>
 *
 *
 * To receive events, an object should:
 *
 *
 *  1. Expose a public method, known as the *event subscriber*, which accepts a single
 * argument of the type of event desired;
 *  1. Mark it with a [Subscribe] annotation;
 *  1. Pass itself to an EventBus instance's [.register] method.
 *
 *
 * <h2>Posting Events</h2>
 *
 *
 * To post an event, simply provide the event object to the [.post] method. The
 * EventBus instance will determine the type of event and route it to all registered listeners.
 *
 *
 * Events are routed based on their type  an event will be delivered to any subscriber for
 * any type to which the event is *assignable.* This includes implemented interfaces, all
 * superclasses, and all interfaces implemented by superclasses.
 *
 *
 * When `post` is called, all registered subscribers for an event are run in sequence, so
 * subscribers should be reasonably quick. If an event may trigger an extended process (such as a
 * database load), spawn a thread or queue it for later. (For a convenient way to do this, use an
 * [AsyncMessageBus].)
 *
 * <h2>Subscriber Methods</h2>
 *
 *
 * Event subscriber methods must accept only one argument: the event.
 *
 *
 * Subscribers should not, in general, throw. If they do, the EventBus will catch and log the
 * exception. This is rarely the right solution for error handling and should not be relied upon; it
 * is intended solely to help find problems during development.
 *
 *
 * The EventBus guarantees that it will not call a subscriber method from multiple threads
 * simultaneously, unless the method explicitly allows it by bearing the [ ] annotation. If this annotation is not present, subscriber methods need not
 * worry about being reentrant, unless also called from outside the EventBus.
 *
 * <h2>Dead Events</h2>
 *
 *
 * If an event is posted, but no registered subscribers can accept it, it is considered "dead."
 * To give the system a second chance to handle dead events, they are wrapped in an instance of
 * [DeadMessage] and reposted.
 *
 *
 * If a subscriber for a supertype of all events (such as Object) is registered, no event will
 * ever be considered dead, and no DeadEvents will be generated. Accordingly, while DeadMessage
 * extends [Object], a subscriber registered to receive any Object will never receive a
 * DeadMessage.
 *
 *
 * This class is safe for concurrent use.
 *
 *
 * See the Guava User Guide article on [`EventBus`](https://github.com/google/guava/wiki/EventBusExplained).
 *
 * @author Cliff
 * @param identifier a brief name for this bus, for logging purposes. Should/home/alex/privacy/project/myths/gaea be a valid Java
 * @param executor the default executor this event bus uses for dispatching events to subscribers.
 * @param dispatcher message dispatcher.
 * @param exceptionHandler Handler for subscriber exceptions.
 */
open class MessageBus<T : Any> internal constructor(
    override val identifier: String,
    override val executor: Executor,
    val dispatcher: Dispatcher<T>,
    val exceptionHandler: SubscriberExceptionHandler<T>,
) : IMessageBus<T> {
    val DEAD_TOPIC = "DEAD_TOPIC"

    val subscribers = TopicSubscriberRegistry(this)

    /**
     * Creates a new EventBus with the given `identifier`.
     *
     * @param identifier a brief name for this bus, for logging purposes. Should/home/alex/privacy/project/myths/gaea be a valid Java
     * identifier.
     */
    @JvmOverloads
    constructor(identifier: String = "default") : this(
        identifier,
        Executor { it.run() },
        Dispatcher.perThreadDispatchQueue(),
        LoggingHandler()
    )

    /**
     * Creates a new EventBus with the given [SubscriberExceptionHandler].
     *
     * @param exceptionHandler Handler for subscriber exceptions.
     * @since 16.0
     */
    constructor(exceptionHandler: SubscriberExceptionHandler<T>) : this(
        "default",
        Executor { it.run() },
        Dispatcher.perThreadDispatchQueue(),
        exceptionHandler
    )

    override fun register(topics: Array<String>, subscriber: Any) {
        subscribers.register(topics, subscriber)
    }

    /**
     * Registers subscriber method on `object` to receive messages.
     *
     * @param topics method subscribe topic.
     * @param subscriber  subscriber method declare object.
     * @param method subscriber method should be registered.
     */
    override fun register(topics: Array<String>, subscriber: Any, method: Method) {
        subscribers.register(topics, subscriber, method)
    }


    /**
     * Registers all subscriber methods on `object` to receive messages.
     *
     * @param subscriber  object whose subscriber methods should be registered.
     */
    override fun register(subscriber: Any) {
        subscribers.register(subscriber)
    }

    override fun register(subscriber: Any, method: Method) {
        subscribers.register(subscriber, method)
    }

    /**
     * Unregisters all subscriber methods on a registered `object`.
     *
     * @param subscriber  object whose subscriber methods should be unregistered.
     * @throws IllegalArgumentException if the object was not previously registered.
     */
    override fun unregister(subscriber: Any) {
        subscribers.unregister(subscriber)
    }

    override fun unregister(topic: String, subscriber: Any) {
        subscribers.unregister(topic, subscriber)
    }

    /**
     * Posts an message to all registered subscribers. This method will return successfully after the
     * message has been posted to all subscribers, and regardless of any exceptions thrown by
     * subscribers.
     *
     * @param message message to post.
     */
    override fun publish(message: T) {
        val messageSubscribers = subscribers.getSubscribers(message.javaClass.name)
        if (messageSubscribers.hasNext()) {
            dispatcher.dispatch(message, messageSubscribers)
        } else {
            // the message had no subscribers and was not itself a DeadMessage
            publish(DEAD_TOPIC, message)
        }
    }

    override fun publish(topic: String, message: T) {
        val messageSubscribers = subscribers.getSubscribers(topic)
        if (messageSubscribers.hasNext()) {
            dispatcher.dispatch(message, messageSubscribers)
        } else if (topic != DEAD_TOPIC) {
            // the message had no subscribers and was not itself a DeadMessage
            publish(DEAD_TOPIC, message)
        }
    }


    /** Handles the given exception thrown by a subscriber with the given context.  */
    override fun handleException(cause: Throwable?, context: SubscriberExceptionContext<T>) {
        try {
            exceptionHandler.handleException(cause, context)
        } catch (e2: Throwable) {
            // if the handler threw an exception... well, just log it
            logger.log(
                Level.SEVERE, String.format(Locale.ROOT, "Exception %s thrown while handling exception: %s", e2, cause),
                e2
            )
        }
    }

    override fun toString(): String {
        return "MessageBus(identifier='$identifier', executor=$executor, dispatcher=$dispatcher, exceptionHandler=$exceptionHandler, subscribers=$subscribers)"
    }

    companion object {
        private val logger = Logger.getLogger(Bus::class.java.name)
    }
}