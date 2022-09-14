
package com.synebula.gaea.bus

import java.lang.reflect.Method

/**
 * Context for an exception thrown by a subscriber.
 *
 * @since 16.0
 *
 * @param bus         The [IBus] that handled the message and the subscriber. Useful for
 * broadcasting a new message based on the error.
 * @param message            The message object that caused the subscriber to throw.
 * @param subscriber       The source subscriber context.
 * @param subscriberMethod the subscribed method.
 */
class SubscriberExceptionContext<T : Any> internal constructor(
    val bus: IBus<T>, val message: Any, val subscriber: Any, val subscriberMethod: Method,
)