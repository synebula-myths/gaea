
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
 * To react to messages, we recommend a reactive-streams framework Like [RxJava](https://github.com/ReactiveX/RxJava/wiki) (supplemented with its [RxAndroid](https://github.com/ReactiveX/RxAndroid) extension if you are building for
 * Android) or [Project Reactor](https://projectreactor.io/). (For the basics of
 * translating code from using a message bus to using a reactive-streams framework, see these two
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
 *  * It uses reflection in ways that break when code is processed by optimizers/minimizer Like
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
 *  * It imposes requirements on the lifecycle of its subscribers. For example, if a message
 * occurs between when one subscriber is removed and the next subscriber is added, the message
 * is dropped.
 *  * Its performance is suboptimal, especially under Android.
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
 * To post a message, simply provide the message object to the [.post] method. The
 * MessageBus instance will determine the type of message and route it to all registered listeners.
 *
 *
 * Messages are routed based on their type  a message will be delivered to any subscriber for
 * any type to which the message is *assignable.* This includes implemented interfaces, all
 * thisclasses, and all interfaces implemented by thisclasses.
 *
 *
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
 * If a message is posted, but no registered subscribers can accept it, it is considered "dead."
 * To give the system a second chance to handle dead messages, they are wrapped in an instance of
 * [DeadMessage] and reposted.
 *
 *
 * If a subscriber for this type of all messages (such as Object) is registered, no message will
 * ever be considered dead, and no DeadMessages will be generated. Accordingly, while DeadMessage
 * extends [Object], a subscriber registered to receive any Object will never receive a
 * DeadMessage.
 *
 *
 * This class is safe for concurrent use.
 *
 *
 * @author Cliff
 * @since 10.0
 * @param identifier a brief name for this bus, for logging purposes. Should/home/alex/privacy/project/myths/gaea be a valid Java
 * @param executor the Default executor this event bus uses for dispatching events to subscribers.
 * @param dispatcher message dispatcher.
 * @param exceptionHandler Handler for subscriber exceptions.
 */
open class Bus<T : Any>(
    override val identifier: String,
    override val executor: Executor,
    val dispatcher: Dispatcher<T>,
    val exceptionHandler: SubscriberExceptionHandler<T>,
) : IBus<T> {

    val DEAD_TOPIC = "DEAD_TOPIC"

    private val subscribers: SubscriberRegistry<T> = SubscriberRegistry(this)

    /**
     * Creates a new MessageBus with the given `identifier`.
     *
     * @param identifier a brief name for this bus, for logging purposes. Should/home/alex/privacy/project/myths/gaea be a valid Java
     * identifier.
     */
    @JvmOverloads
    constructor(identifier: String = "Default") : this(
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
    constructor(exceptionHandler: SubscriberExceptionHandler<T>) : this(
        "Default",
        Executor { it.run() },
        Dispatcher.perThreadDispatchQueue(),
        exceptionHandler
    )

    /**
     * Creates a new AsyncMessageBus that will use `executor` to dispatch messages. Assigns `identifier` as the bus's name for logging purposes.
     *
     * @param identifier short name for the bus, for logging purposes.
     * @param executor Executor to use to dispatch messages. It is the caller's responsibility to shut
     * down the executor after the last message has been posted to this message bus.
     */
    constructor(identifier: String, executor: Executor) : this(
        identifier,
        executor,
        Dispatcher.legacyAsync(),
        LoggingHandler()
    )

    /**
     * Creates a new AsyncMessageBus that will use `executor` to dispatch messages.
     *
     * @param executor Executor to use to dispatch messages. It is the caller's responsibility to shut
     * down the executor after the last message has been posted to this message bus.
     * @param subscriberExceptionHandler Handler used to handle exceptions thrown from subscribers.
     * See [SubscriberExceptionHandler] for more information.
     * @since 16.0
     */
    constructor(executor: Executor, subscriberExceptionHandler: SubscriberExceptionHandler<T>) : this(
        "Default",
        executor,
        Dispatcher.legacyAsync(),
        subscriberExceptionHandler
    )

    /**
     * Creates a new AsyncMessageBus that will use `executor` to dispatch messages.
     *
     * @param executor Executor to use to dispatch messages. It is the caller's responsibility to shut
     * down the executor after the last message has been posted to this message bus.
     */
    constructor(executor: Executor) : this(
        "Default",
        executor,
        Dispatcher.legacyAsync(),
        LoggingHandler()
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
     * Posts a message to all registered subscribers. This method will return successfully after the
     * message has been posted to all subscribers, and regardless of any exceptions thrown by
     * subscribers.
     *
     * @param message message to post.
     */
    override fun publish(message: T) {
        val messageSubscribers = subscribers.getSubscribers(message::class.java.name)
        if (messageSubscribers.hasNext()) {
            dispatcher.dispatch(message, messageSubscribers)
        } else {
            // the message had no subscribers and was not itself a DeadMessage
            publish(DEAD_TOPIC, message)
        }
    }

    /**
     * Posts a message to all registered subscribers. This method will return successfully after the
     * message has been posted to all subscribers, and regardless of any exceptions thrown by
     * subscribers.
     *
     * @param message message to post.
     */
    override fun publishAsync(message: T) {
        val messageSubscribers = subscribers.getSubscribers(message::class.java.name)
        if (messageSubscribers.hasNext()) {
            dispatcher.dispatchAsync(message, messageSubscribers)
        } else {
            // the message had no subscribers and was not itself a DeadMessage
            publishAsync(DEAD_TOPIC, message)
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

    override fun publishAsync(topic: String, message: T) {
        val messageSubscribers = subscribers.getSubscribers(topic)
        if (messageSubscribers.hasNext()) {
            dispatcher.dispatchAsync(message, messageSubscribers)
        } else if (topic != DEAD_TOPIC) {
            // the message had no subscribers and was not itself a DeadMessage
            publishAsync(DEAD_TOPIC, message)
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
        return "Bus(identifier='$identifier', executor=$executor, dispatcher=$dispatcher, exceptionHandler=$exceptionHandler, subscribers=$subscribers)"
    }

    companion object {
        private val logger = Logger.getLogger(Bus::class.java.name)
    }
}