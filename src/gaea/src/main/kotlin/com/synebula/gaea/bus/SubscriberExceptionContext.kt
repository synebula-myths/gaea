/*
 * Copyright (C) 2013 The Guava Authors
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