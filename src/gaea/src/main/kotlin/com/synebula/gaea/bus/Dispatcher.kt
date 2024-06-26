
package com.synebula.gaea.bus

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Handler for dispatching messages to subscribers, providing different message ordering guarantees that
 * make sense for different situations.
 *
 *
 * **Note:** The dispatcher is orthogonal to the subscriber's `Executor`. The dispatcher
 * controls the order in which messages are dispatched, while the executor controls how (i.e. on which
 * thread) the subscriber is actually called when a message is dispatched to it.
 *
 * @author Colin Decker
 */
abstract class Dispatcher<T : Any> {
    /** Dispatches the given `message` to the given `subscribers`.  */
    fun dispatch(message: T, subscribers: Iterator<Subscriber<T>>?) {
        while (subscribers!!.hasNext()) {
            subscribers.next().dispatch(message)
        }
    }

    /** Dispatches the given `message` to the given `subscribers`.  */
    abstract fun dispatchAsync(message: T, subscribers: Iterator<Subscriber<T>>?)

    /** Implementation of a [.perThreadDispatchQueue] dispatcher.  */
    private class PerThreadQueuedDispatcher<T : Any> : Dispatcher<T>() {
        // This dispatcher matches the original dispatch behavior of MessageBus.
        /** Per-thread queue of messages to dispatch.  */
        private val queue: ThreadLocal<Queue<Message<T>>> = object : ThreadLocal<Queue<Message<T>>>() {
            override fun initialValue(): Queue<Message<T>> {
                return ArrayDeque()
            }
        }

        /** Per-thread dispatch state, used to avoid reentrant message dispatching.  */
        private val dispatching: ThreadLocal<Boolean> = object : ThreadLocal<Boolean>() {
            override fun initialValue(): Boolean {
                return false
            }
        }

        override fun dispatchAsync(message: T, subscribers: Iterator<Subscriber<T>>?) {
            val queueForThread = queue.get()
            queueForThread.offer(Message(message, subscribers))
            if (!dispatching.get()) {
                dispatching.set(true)
                try {
                    var nextMessage: Message<T>?
                    while (queueForThread.poll().also { nextMessage = it } != null) {
                        while (nextMessage!!.subscribers!!.hasNext()) {
                            nextMessage!!.subscribers!!.next().dispatchAsync(nextMessage!!.message)
                        }
                    }
                } finally {
                    dispatching.remove()
                    queue.remove()
                }
            }
        }

        private class Message<T : Any>(val message: Any, val subscribers: Iterator<Subscriber<T>>?)
    }

    /** Implementation of a [.legacyAsync] dispatcher.  */
    private class LegacyAsyncDispatcher<T : Any> : Dispatcher<T>() {
        // This dispatcher matches the original dispatch behavior of AsyncMessageBus.
        //
        // We can't really make any guarantees about the overall dispatch order for this dispatcher in
        // a multithreaded environment for a couple reasons:
        //
        // 1. Subscribers to messages posted on different threads can be interleaved with each other
        //    freely. (A message on one thread, B message on another could yield any of
        //    [a1, a2, a3, b1, b2], [a1, b2, a2, a3, b2], [a1, b2, b3, a2, a3], etc.)
        // 2. It's possible for subscribers to actually be dispatched to in a different order than they
        //    were added to the queue. It's easily possible for one thread to take the head of the
        //    queue, immediately followed by another thread taking the next element in the queue. That
        //    second thread can then dispatch to the subscriber it took before the first thread does.
        //
        // All this makes me really wonder if there's any value in queueing here at all. A dispatcher
        // that simply loops through the subscribers and dispatches the message to each would actually
        // probably provide a stronger order guarantee, though that order would obviously be different
        // in some cases.
        /** Global message queue.  */
        private val queue = ConcurrentLinkedQueue<MessageWithSubscriber<T>>()
        override fun dispatchAsync(message: T, subscribers: Iterator<Subscriber<T>>?) {
            while (subscribers!!.hasNext()) {
                queue.add(MessageWithSubscriber(message, subscribers.next()))
            }
            var e: MessageWithSubscriber<T>?
            while (queue.poll().also { e = it } != null) {
                e!!.subscriber!!.dispatchAsync(e!!.message)
            }
        }

        private class MessageWithSubscriber<T : Any>(val message: T, val subscriber: Subscriber<T>?)
    }

    companion object {
        /**
         * Returns a dispatcher that queues messages that are posted reentrantly on a thread that is already
         * dispatching a message, guaranteeing that all messages posted on a single thread are dispatched to
         * all subscribers in the order they are posted.
         *
         *
         * When all subscribers are dispatched to using a *direct* executor (which dispatches on
         * the same thread that posts the message), this yields a breadth-first dispatch order on each
         * thread. That is, all subscribers to a single message A will be called before any subscribers to
         * any messages B and C that are posted to the message bus by the subscribers to A.
         */
        fun <T : Any> perThreadDispatchQueue(): Dispatcher<T> {
            return PerThreadQueuedDispatcher()
        }

        /**
         * Returns a dispatcher that queues messages that are posted in a single global queue. This behavior
         * matches the original behavior of AsyncMessageBus exactly, but is otherwise not especially useful.
         * For async dispatch, an [immediate][.immediate] dispatcher should generally be
         * preferable.
         */
        fun <T : Any> legacyAsync(): Dispatcher<T> {
            return LegacyAsyncDispatcher()
        }
    }
}