/*
 * Copyright (C) 2014 The Guava Authors
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

import com.synebula.gaea.exception.NoticeUserException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.Executor

/**
 * A subscriber method on a specific object, plus the executor that should be used for dispatching
 * messages to it.
 *
 *
 * Two subscribers are equivalent when they refer to the same method on the same object (not
 * class). This property is used to ensure that no subscriber method is registered more than once.
 *
 * @author Colin Decker
 *
 * @param bus      The message bus this subscriber belongs to.
 * @param target      The object with the subscriber method.
 * @param method Subscriber method.
 */
open class Subscriber<T : Any> private constructor(
    private val bus: IBus<T>,
    val target: Any,
    val method: Method,
) {
    /** Executor to use for dispatching messages to this subscriber.  */
    private val executor: Executor?

    init {
        method.isAccessible = true
        executor = bus.executor
    }

    /** Dispatches `message` to this subscriber .  */
    fun dispatch(message: Any) {
        invokeSubscriberMethod(message)
    }

    /** Dispatches `message` to this subscriber using the proper executor.  */
    fun dispatchAsync(message: Any) {
        executor!!.execute {
            try {
                invokeSubscriberMethod(message)
            } catch (e: InvocationTargetException) {
                bus.handleException(e.cause, context(message))
            }
        }
    }

    /**
     * Invokes the subscriber method. This method can be overridden to make the invocation
     * synchronized.
     */
    @Throws(InvocationTargetException::class)
    open fun invokeSubscriberMethod(message: Any) {
        try {
            method.invoke(target, message)
        } catch (e: IllegalArgumentException) {
            throw Error("Method rejected target/argument: $message", e)
        } catch (e: IllegalAccessException) {
            throw Error("Method became inaccessible: $message", e)
        } catch (e: InvocationTargetException) {
            if (e.cause is Error || e.cause is NoticeUserException) {
                throw e.cause!!
            }
            throw e
        }
    }

    /** Gets the context for the given message.  */
    private fun context(message: Any): SubscriberExceptionContext<T> {
        return SubscriberExceptionContext(bus, message, target, method)
    }

    override fun hashCode(): Int {
        return (31 + method.hashCode()) * 31 + System.identityHashCode(target)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Subscriber<*>) {
            // Use == so that different equal instances will still receive messages.
            // We only guard against the case that the same object is registered
            // multiple times
            return target === other.target && method == other.method
        }
        return false
    }

    /**
     * Subscriber that synchronizes invocations of a method to ensure that only one thread may enter
     * the method at a time.
     */
    internal class SynchronizedSubscriber<T : Any>(bus: IBus<T>, target: Any, method: Method) :
        Subscriber<T>(bus, target, method) {
        @Throws(InvocationTargetException::class)
        override fun invokeSubscriberMethod(message: Any) {
            synchronized(this) { super.invokeSubscriberMethod(message) }
        }
    }

    companion object {
        /** Creates a `Subscriber` for `method` on `subscriber`.  */
        fun <T : Any> create(bus: IBus<T>, subscriber: Any, method: Method): Subscriber<T> {
            return if (isDeclaredThreadSafe(method)) Subscriber(
                bus,
                subscriber,
                method
            ) else SynchronizedSubscriber(
                bus, subscriber, method
            )
        }

        /**
         * Checks whether `method` is thread-safe, as indicated by the presence of the [ ] annotation.
         */
        private fun isDeclaredThreadSafe(method: Method): Boolean {
            return method.getAnnotation(AllowConcurrentSubscribe::class.java) != null
        }
    }
}
