
package com.synebula.gaea.bus

/**
 * Marks a method as a message subscriber.
 *
 *
 * The type of message will be indicated by the method's first (and only) parameter, which cannot
 * be primitive. If this annotation is applied to methods with zero parameters, or more than one
 * parameter, the object containing the method will not be able to register for message delivery from
 * the [Bus].
 *
 *
 * Unless also annotated with @[AllowConcurrentSubscribe], message subscriber methods will be
 * invoked serially by each message bus that they are registered with.
 *
 * @author Cliff
 * @since 10.0
 *
 * @param topics method subscribe topics
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Subscribe(val topics: Array<String> = [])