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

import com.synebula.gaea.reflect.supertypes
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Registry of subscribers to a single message bus.
 *
 * @author Colin Decker
 */
open class SubscriberRegistry<T : Any>(private val bus: IBus<T>) {
    /**
     * All registered subscribers, indexed by message type.
     *
     *
     * The [CopyOnWriteArraySet] values make it easy and relatively lightweight to get an
     * immutable snapshot of all current subscribers to a message without any locking.
     */
    private val subscribers = ConcurrentHashMap<Class<*>, CopyOnWriteArraySet<Subscriber<T>>>()


    /** Registers all subscriber methods on the given subscriber object.  */
    open fun register(subscriber: Any) {
        val listenerMethods = findAllSubscribers(subscriber)
        for ((eventType, messageMethodsInListener) in listenerMethods) {
            var messageSubscribers = subscribers[eventType]
            if (messageSubscribers == null) {
                val newSet = CopyOnWriteArraySet<Subscriber<T>>()
                messageSubscribers = subscribers.putIfAbsent(eventType, newSet) ?: newSet
            }
            messageSubscribers.addAll(messageMethodsInListener)
        }
    }

    /** Registers subscriber method on the given subscriber object.  */
    open fun register(subscriber: Any, method: Method) {
        val parameterTypes = method.parameterTypes
        val eventType = parameterTypes[0]
        check(parameterTypes.size == 1) {
            "Method $method has @SubscribeTopic annotation but has ${parameterTypes.size} parameters. Subscriber methods must have exactly 1 parameter."
        }
        check(!parameterTypes[0].isPrimitive) {
            "@SubscribeTopic method $method's parameter is ${parameterTypes[0].name}. Subscriber methods cannot accept primitives. "
        }
        var messageSubscribers = subscribers[eventType]
        if (messageSubscribers == null) {
            val newSet = CopyOnWriteArraySet<Subscriber<T>>()
            messageSubscribers = subscribers.putIfAbsent(eventType, newSet) ?: newSet
        }
        messageSubscribers.add(Subscriber.create(bus, subscriber, method))
    }


    /** Unregisters all subscribers on the given subscriber object.  */
    open fun unregister(subscriber: Any) {
        val listenerMethods = findAllSubscribers(subscriber)
        for ((eventType, listenerMethodsForType) in listenerMethods) {
            val currentSubscribers = subscribers[eventType]
            require(!(currentSubscribers == null || !currentSubscribers.removeAll(listenerMethodsForType.toSet()))) {
                // if removeAll returns true, all we really know is that at least one subscriber was
                // removed... however, barring something very strange we can assume that if at least one
                // subscriber was removed, all subscribers on subscriber for that message type were... after
                // all, the definition of subscribers on a particular class is totally static
                "missing message subscriber for an annotated method. Is $subscriber registered?"
            }

            // don't try to remove the set if it's empty; that can't be done safely without a lock
            // anyway, if the set is empty it'll just be wrapping an array of length 0
        }
    }

    /**
     * Gets an iterator representing an immutable snapshot of all subscribers to the given message at
     * the time this method is called.
     */
    fun getSubscribers(eventType: Any): Iterator<Subscriber<T>> {
        val eventSubscribers: CopyOnWriteArraySet<Subscriber<T>> =
            subscribers[eventType.javaClass] ?: CopyOnWriteArraySet()
        return eventSubscribers.iterator()
    }

    /**
     * Returns all subscribers for the given subscriber grouped by the type of message they subscribe to.
     */
    private fun findAllSubscribers(subscriber: Any): Map<Class<*>, List<Subscriber<T>>> {
        val methodsInListener = mutableMapOf<Class<*>, List<Subscriber<T>>>()
        val clazz: Class<*> = subscriber.javaClass
        for (method in getAnnotatedMethods(clazz)) {
            val parameterTypes = method.parameterTypes
            val eventType = parameterTypes[0]
            val methods = methodsInListener[eventType]?.toMutableList() ?: mutableListOf()
            methods.add(Subscriber.create(bus, subscriber, method))
            methodsInListener[eventType] = methods
        }
        return methodsInListener.toMap()
    }


    class MethodIdentifier(method: Method) {
        private val name: String = method.name
        private val parameterTypes: List<Class<*>> = listOf(*method.parameterTypes)

        override fun hashCode(): Int {
            var result = name.hashCode()
            for (element in parameterTypes) result = 31 * result + element.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (other is MethodIdentifier) {
                return name == other.name && parameterTypes == other.parameterTypes
            }
            return false
        }
    }

    /**
     * A thread-safe cache that contains the mapping from each class to all methods in that class and
     * all super-classes, that are annotated with `@Subscribe`. The cache is shared across all
     * instances of this class; this greatly improves performance if multiple MessageBus instances are
     * created and objects of the same class are registered on all of them.
     */
    protected val subscriberMethodsCache = mapOf<Class<*>, List<Method>>()

    @Synchronized
    protected fun getAnnotatedMethods(clazz: Class<*>): List<Method> {
        var methods = subscriberMethodsCache[clazz]
        if (methods == null)
            methods = getAnnotatedMethodsNotCached(clazz, Subscribe::class.java)
        return methods
    }

    protected fun getAnnotatedMethodsNotCached(
        clazz: Class<*>,
        annotationClass: Class<out Annotation>,
    ): List<Method> {
        val supertypes = flattenHierarchy(clazz)
        val identifiers = mutableMapOf<MethodIdentifier, Method>()
        for (supertype in supertypes) {
            for (method in supertype.declaredMethods) {
                if (method.isAnnotationPresent(annotationClass) && !method.isSynthetic) {
                    val parameterTypes = method.parameterTypes
                    check(parameterTypes.size == 1) {
                        "Method $method has @SubscribeTopic annotation but has ${parameterTypes.size} parameters. Subscriber methods must have exactly 1 parameter."
                    }
                    check(!parameterTypes[0].isPrimitive) {
                        "@SubscribeTopic method $method's parameter is ${parameterTypes[0].name}. Subscriber methods cannot accept primitives. "
                    }

                    val identifier = MethodIdentifier(method)
                    if (!identifiers.containsKey(identifier)) {
                        identifiers[identifier] = method
                    }
                }
            }
        }
        return identifiers.values.toList()
    }

    companion object {

        /** Global cache of classes to their flattened hierarchy of supertypes.  */
        protected val flattenHierarchyCache = mutableMapOf<Class<*>, Set<Class<*>>>()

        /**
         * Flattens a class's type hierarchy into a set of `Class` objects including all
         * superclasses (transitively) and all interfaces implemented by these superclasses.
         */
        fun flattenHierarchy(clazz: Class<*>): Set<Class<*>> {
            var supertypes = flattenHierarchyCache[clazz]
            if (supertypes == null) {
                supertypes = clazz.supertypes()
                flattenHierarchyCache[clazz] = supertypes
            }
            return supertypes
        }
    }
}