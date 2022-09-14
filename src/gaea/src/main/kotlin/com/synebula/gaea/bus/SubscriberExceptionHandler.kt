
package com.synebula.gaea.bus

/**
 * Handler for exceptions thrown by message subscribers.
 *
 * @since 16.0
 */
interface SubscriberExceptionHandler<T : Any> {
    /** Handles exceptions thrown by subscribers.  */
    fun handleException(exception: Throwable?, context: SubscriberExceptionContext<T>)
}