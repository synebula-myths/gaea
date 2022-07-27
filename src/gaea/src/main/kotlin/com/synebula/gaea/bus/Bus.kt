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
package com.synebula.gaea.bus

import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.Executor
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Dispatches messages to listeners, and provides ways for listeners to register themselves.
 *
 * <h2>Avoid MessageBus</h2>
 *
 *
 * **We recommend against using MessageBus.** It was designed many years ago, and newer
 * libraries offer better ways to decouple components and react to messages.
 *
 *
 * To decouple components, we recommend a dependency-injection framework. For Android code, most
 * apps use [Dagger](https://dagger.dev). For server code, common options include [Guice](https://github.com/google/guice/wiki/Motivation) and [Spring](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-introduction).
 * Frameworks typically offer a way to register multiple listeners independently and then request
 * them together as a set ([Dagger](https://dagger.dev/dev-guide/multibindings), [Guice](https://github.com/google/guice/wiki/Multibindings), [Spring](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-autowired-annotation)).
 *
 *
 * To react to messages, we recommend a reactive-streams framework like [RxJava](https://github.com/ReactiveX/RxJava/wiki) (supplemented with its [RxAndroid](https://github.com/ReactiveX/RxAndroid) extension if you are building for
 * Android) or [Project Reactor](https://projectreactor.io/). (For the basics of
 * translating code from using an message bus to using a reactive-streams framework, see these two
 * guides: [1](https://blog.jkl.gg/implementing-an-message-bus-with-rxjava-rxbus/), [2](https://lorentzos.com/rxjava-as-message-bus-the-right-way-10a36bdd49ba).) Some usages
 * of MessageBus may be better written using [Kotlin coroutines](https://kotlinlang.org/docs/coroutines-guide.html), including [Flow](https://kotlinlang.org/docs/flow.html) and [Channels](https://kotlinlang.org/docs/channels.html). Yet other usages are better served
 * by individual libraries that provide specialized support for particular use cases.
 *
 *
 * Disadvantages of MessageBus include:
 *
 *
 *  * It makes the cross-references between producer and subscriber harder to find. This can
 * complicate debugging, lead to unintentional reentrant calls, and force apps to eagerly
 * initialize all possible subscribers at startup time.
 *  * It uses reflection in ways that break when code is processed by optimizers/minimizer like
 * [R8 and Proguard](https://developer.android.com/studio/build/shrink-code).
 *  * It doesn't offer a way to wait for multiple messages before taking action. For example, it
 * doesn't offer a way to wait for multiple producers to all report that they're "ready," nor
 * does it offer a way to batch multiple messages from a single producer together.
 *  * It doesn't support backpressure and other features needed for resilience.
 *  * It doesn't provide much control of threading.
 *  * It doesn't offer much monitoring.
 *  * It doesn't propagate exceptions, so apps don't have a way to react to them.
 *  * It doesn't interoperate well with RxJava, coroutines, and other more commonly used
 * alternatives.
 *  * It imposes requirements on the lifecycle of its subscribers. For example, if an message
 * occurs between when one subscriber is removed and the next subscriber is added, the message
 * is dropped.
 *  * Its performance is suboptimal, especially under Android.
 *  * It [doesn't support parameterized
 * types](https://github.com/google/guava/issues/1431).
 *  * With the introduction of lambdas in Java 8, MessageBus went from less verbose than listeners
 * to [more verbose](https://github.com/google/guava/issues/3311).
 *
 *
 * <h2>MessageBus Summary</h2>
 *
 *
 * The MessageBus allows publish-subscribe-style communication between components without requiring
 * the components to explicitly register with one another (and thus be aware of each other). It is
 * designed exclusively to replace traditional Java in-process message distribution using explicit
 * registration. It is *not* a general-purpose publish-subscribe system, nor is it intended
 * for interprocess communication.
 *
 * <h2>Receiving Messages</h2>
 *
 *
 * To receive messages, an object should:
 *
 *
 *  1. Expose a public method, known as the *message subscriber*, which accepts a single
 * argument of the type of message desired;
 *  1. Mark it with a [Subscribe] annotation;
 *  1. Pass itself to an MessageBus instance's [.register] method.
 *
 *
 * <h2>Posting Messages</h2>
 *
 *
 * To post an message, simply provide the message object to the [.post] method. The
 * MessageBus instance will determine the type of message and route it to all registered listeners.
 *
 *
 * Messages are routed based on their type  an message will be delivered to any subscriber for
 * any type to which the message is *assignable.* This includes implemented interfaces, all
 * superclasses, and all interfaces implemented by superclasses.
 *
 *
 * When `post` is called, all registered subscribers for an message are run in sequence, so
 * subscribers should be reasonably quick. If an message may trigger an extended process (such as a
 * database load), spawn a thread or queue it for later. (For a convenient way to do this, use an
 * [AsyncBus].)
 *
 * <h2>Subscriber Methods</h2>
 *
 *
 * Message subscriber methods must accept only one argument: the message.
 *
 *
 * Subscribers should not, in general, throw. If they do, the MessageBus will catch and log the
 * exception. This is rarely the right solution for error handling and should not be relied upon; it
 * is intended solely to help find problems during development.
 *
 *
 * The MessageBus guarantees that it will not call a subscriber method from multiple threads
 * simultaneously, unless the method explicitly allows it by bearing the [ ] annotation. If this annotation is not present, subscriber methods need not
 * worry about being reentrant, unless also called from outside the MessageBus.
 *
 * <h2>Dead Messages</h2>
 *
 *
 * If an message is posted, but no registered subscribers can accept it, it is considered "dead."
 * To give the system a second chance to handle dead messages, they are wrapped in an instance of
 * [DeadMessage] and reposted.
 *
 *
 * If a subscriber for a supertype of all messages (such as Object) is registered, no message will
 * ever be considered dead, and no DeadMessages will be generated. Accordingly, while DeadMessage
 * extends [Object], a subscriber registered to receive any Object will never receive a
 * DeadMessage.
 *
 *
 * This class is safe for concurrent use.
 *
 *
 * See the Guava User Guide article on [`MessageBus`](https://github.com/google/guava/wiki/MessageBusExplained).
 *
 * @author Cliff
 * @since 10.0
 * @param identifier a brief name for this bus, for logging purposes. Should/home/alex/privacy/project/myths/gaea be a valid Java
 * @param executor the default executor this event bus uses for dispatching events to subscribers.
 * @param dispatcher message dispatcher.
 * @param exceptionHandler Handler for subscriber exceptions.
 */
open class Bus(
    override val identifier: String,
    override val executor: Executor,
    val dispatcher: Dispatcher<Any>,
    val exceptionHandler: SubscriberExceptionHandler<Any>,
) : IBus<Any> {

    private val subscribers: SubscriberRegistry<Any> = SubscriberRegistry(this)

    /**
     * Creates a new MessageBus with the given `identifier`.
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
     * Creates a new MessageBus with the given [SubscriberExceptionHandler].
     *
     * @param exceptionHandler Handler for subscriber exceptions.
     * @since 16.0
     */
    constructor(exceptionHandler: SubscriberExceptionHandler<Any>) : this(
        "default",
        Executor { it.run() },
        Dispatcher.perThreadDispatchQueue(),
        exceptionHandler
    )


    /**
     * Registers all subscriber methods on `object` to receive messages.
     *
     * @param subscriber object whose subscriber methods should be registered.
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
     * @param subscriber object whose subscriber methods should be unregistered.
     * @throws IllegalArgumentException if the object was not previously registered.
     */
    override fun unregister(subscriber: Any) {
        subscribers.unregister(subscriber)
    }

    /**
     * Posts an message to all registered subscribers. This method will return successfully after the
     * message has been posted to all subscribers, and regardless of any exceptions thrown by
     * subscribers.
     *
     *
     * If no subscribers have been subscribed for `message`'s class, and `message` is not
     * already a [DeadMessage], it will be wrapped in a DeadMessage and reposted.
     *
     * @param message message to post.
     */
    override fun publish(message: Any) {
        val messageSubscribers = subscribers.getSubscribers(message)
        if (messageSubscribers.hasNext()) {
            dispatcher.dispatch(message, messageSubscribers)
        } else if (message !is DeadMessage) {
            // the message had no subscribers and was not itself a DeadMessage
            publish(DeadMessage(this, message))
        }
    }


    /** Handles the given exception thrown by a subscriber with the given context.  */
    override fun handleException(cause: Throwable?, context: SubscriberExceptionContext<Any>) {
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
        return "Bus(identifier='$identifier', executor=$executor, dispatcher=$dispatcher, exceptionHandler=$exceptionHandler, subscribers=$subscribers)"
    }

    companion object {
        private val logger = Logger.getLogger(Bus::class.java.name)
    }
}