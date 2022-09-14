
package com.synebula.gaea.bus

/**
 * Marks a message subscriber method as being thread-safe. This annotation indicates that MessageBus
 * may invoke the message subscriber simultaneously from multiple threads.
 *
 *
 * This does not mark the method, and so should be used in combination with [Subscribe].
 *
 * @author Cliff
 * @since 10.0
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class AllowConcurrentSubscribe