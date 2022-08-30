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

import kotlin.reflect.KClass

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
 * @param messageClass class of message
 * @param domainClass subscriber for class of domain
 * @param domain subscriber for domain
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class DomainSubscribe(
    val messageClass: KClass<*>,
    val domainClass: KClass<*> = Nothing::class,
    val domain: String = ""
)